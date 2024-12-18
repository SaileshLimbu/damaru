package com.powersoft.common.webrtc

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.util.Log
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
import com.powersoft.common.model.GestureAction
import com.powersoft.common.model.GestureCommand
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.Observer
import org.webrtc.ScreenCapturerAndroid
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoTrack
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class WebRTCClient @Inject constructor(
    private val context: Context
) {
    private val webRTCManager = WebRTCManager(context)
    private var peerConnection: PeerConnection? = null
    var dataChannel: DataChannel? = null
    private lateinit var webRTCListener: WebRTCListener
    private lateinit var username: String

    //Damaru Server App
    private var screenCapturer: VideoCapturer? = null
    private var localStream: MediaStream? = null
    private var localVideoTrack: VideoTrack? = null
    private val localStreamId = "local_stream"

    private lateinit var targetUser: String
    private lateinit var intent: Intent

    private val mediaConstraint = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"))
    }

    private val iceServers = listOf(
//        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
        PeerConnection.IceServer.builder("turn:openrelay.metered.ca:443?transport=tcp")
            .setUsername("openrelayproject")
            .setPassword("openrelayproject")
            .createIceServer()
    )

    fun setTargetUser(target: String) {
        this.targetUser = target
    }

    fun init(webRTCListener: WebRTCListener, username: String, intent: Intent? = null, observer: Observer) {
        this.webRTCListener = webRTCListener
        this.username = username

        peerConnection = webRTCManager.createPeerConnection(iceServers, observer)

        /**
         * if init is called from server app then set intent
         * else if it's from client app then createDataChannel()
         * we don't need to create data channel on both end. WebRTC auto creates on the other side
         * just need to use onDataChannel() callbacks from webRTC
         */
        intent?.let {
            this.intent = it
            startScreenCapturing()
        } ?: createDataChannel()
    }

    private fun createDataChannel() {
        dataChannel = peerConnection?.createDataChannel("dataChannel", DataChannel.Init())
        dataChannel?.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(p0: Long) {
                Log.d(TAG, "onBufferedAmountChange >> $p0")
            }

            override fun onStateChange() {
                Log.d(TAG, "onStateChange >> ${dataChannel?.state()}")
                if (dataChannel?.state() == DataChannel.State.OPEN){
                    webRTCListener.onDataChannelConnected()
                }
            }

            override fun onMessage(p0: DataChannel.Buffer?) {
                p0.let {
                    val bytes = ByteArray(p0!!.data.remaining())
                    p0.data.get(bytes)
                    val message = String(bytes, StandardCharsets.UTF_8)
                    webRTCListener.onChannelMessage(message)
                }
            }
        })
        Log.d(TAG, "Data Channel Created !!! $dataChannel")
    }

    /**
     * Send message from one peer to another peer for remote control etc.
     */
    fun sendDataMessage(mDataChannel: DataChannel?, message: String) {
        val buffer = ByteBuffer.wrap(message.toByteArray())
        mDataChannel?.send(DataChannel.Buffer(buffer, false))
        Log.d(TAG, "sendDataMessage >>> $message")
    }

    /**
     * This function is only used by server app to capture the screen and send them to the stream
     */
    private fun startScreenCapturing() {
        val metrics = context.resources.displayMetrics
        val screenWidthPixels = metrics.widthPixels
        val screenHeightPixels = metrics.heightPixels

        val surfaceTextureHelper = SurfaceTextureHelper.create(
            Thread.currentThread().name, webRTCManager.getEglBase().eglBaseContext
        )

        screenCapturer = ScreenCapturerAndroid(intent, object : MediaProjection.Callback() {})
        val videoSource = webRTCManager.createVideoSource()
        screenCapturer?.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
        screenCapturer?.startCapture(screenWidthPixels, screenHeightPixels, 60)

        val localVideoTrack = webRTCManager.createVideoTrack(videoSource)

        localStream = webRTCManager.getMyPeerConnectionFactory().createLocalMediaStream(localStreamId)
        localStream?.addTrack(localVideoTrack)
        if (localStream != null) {
            Log.d(TAG, "startScreenCapturing: ${localStream.toString()}")
            peerConnection?.addTrack(localVideoTrack, listOf(localStreamId))
        }
    }

    fun getEglBase() = webRTCManager.getEglBase()

    /**
     * Used exclusively by the client app to initiate a WebRTC connection.
     *
     * This function creates an SDP (Session Description Protocol) offer using the PeerConnection.
     * Once the offer is successfully created, it is set as the local description.
     * After setting the local description, the offer is sent to the server via a socket
     * as part of the signaling process.
     *
     * @param target The identifier of the target peer (e.g., username or device ID) to which the offer is sent.
     */
    fun sendOffer(target: String) {
        peerConnection?.createOffer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection?.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        webRTCListener.onTransferEventToSocket(
                            DataModel(
                                type = DataModelType.Offer, username, target, desc?.description
                            )
                        )
                    }
                }, desc)
            }
        }, mediaConstraint)
    }

    /**
     * This function is exclusively used by the server app.
     * It processes the received offer from the client app to establish a WebRTC connection.
     * The function performs the following steps:
     * 1. Creates an Answer response based on the received Offer.
     * 2. Sets the Answer as the localDescription on the peerConnection.
     * 3. Sends the generated Answer back to the client app via a socket connection.
     *
     * @param target The identifier of the target peer (e.g., username or device ID) to which the offer is sent.
     */

    fun createAnswer(target: String) {
        peerConnection!!.createAnswer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection?.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        webRTCListener.onTransferEventToSocket(
                            DataModel(
                                type = DataModelType.Answer,
                                username = username,
                                target = target,
                                data = desc?.description
                            )
                        )
                    }
                }, desc)
            }

            override fun onCreateFailure(p0: String?) {
                super.onCreateFailure(p0)
                Log.d(TAG, "onCreateFailure >>>> $p0")
            }
        }, mediaConstraint)
    }

    /**
     * Handles the received OFFER or ANSWER from the socket.
     * Adds the received session description to the PeerConnection.
     */

    fun onRemoteSessionReceived(sessionDescription: SessionDescription) {
        peerConnection?.setRemoteDescription(MySdpObserver(), sessionDescription)
    }

    /**
     * When we receive ICE candidate from client/server than
     * add this to the peerConnection
     */
    fun addIceCandidate(iceCandidate: IceCandidate) {
        Log.d(TAG, "onIceCandidateAdded: $iceCandidate")
        peerConnection?.addIceCandidate(iceCandidate)
    }

    /**
     * Send the ICE candidate received from webRTC to target user
     */
    fun sendIceCandidate(candidate: IceCandidate, target: String) {
        webRTCListener.onTransferEventToSocket(
            DataModel(
                type = DataModelType.IceCandidates,
                username = username,
                target = target,
                data = Gson().toJson(candidate)
            )
        )
    }

    /**
     * When client disconnects from their site. Close the connection
     * and do some clean up.
     */
    fun closeConnection(target: String) {
        webRTCListener.onTransferEventToSocket(
            DataModel(
                type = DataModelType.EndCall,
                username = username,
                target = target
            )
        )
        disposeClient()
    }

    private fun PeerConnection.removeStreamsTracks(){
        senders.forEach{removeTrack(it)}
    }

    /**
     * When stream is ended then clear these things from server
     * We can initialize these things when the stream starts again
     * TODO: for multiple stream we have to check if some users are still using the stream before disposing
     */
    fun disposeServer() {
        disposeClient()
        screenCapturer?.apply{
            stopCapture()
            dispose()
        }
        localStream?.dispose()
        localVideoTrack?.dispose()

        screenCapturer = null
        localStream = null
        localVideoTrack = null
    }

    /**
     * When user is no longer connected to emulator then clear these two
     * because these 2 are the only thing that are used by client app
     */
    private fun disposeClient() {
        peerConnection?.apply {
            removeStreamsTracks()
            close()
            dispose()
        }
        webRTCManager.dispose()
        dataChannel?.unregisterObserver()
        dataChannel = null
        peerConnection = null
    }

    companion object {
        const val TAG = "DAMARU"
    }
}