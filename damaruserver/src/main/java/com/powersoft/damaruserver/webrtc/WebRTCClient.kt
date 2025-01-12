package com.powersoft.damaruserver.webrtc

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.util.Log
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
import com.powersoft.common.model.GestureCommand
import com.powersoft.common.utils.AspectRatioUtils
import com.powersoft.common.utils.WebRTCUtils
import com.powersoft.common.webrtc.MyPeerObserver
import com.powersoft.common.webrtc.MySdpObserver
import com.powersoft.common.webrtc.WebRTCListener
import com.powersoft.common.webrtc.WebRTCManager
import com.powersoft.damaruserver.service.DeviceControlService
import com.powersoft.damaruserver.service.ScreenCaptureForegroundService
import com.powersoft.damaruserver.utils.ScreenRefresher
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.ScreenCapturerAndroid
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoTrack
import java.nio.charset.StandardCharsets
import javax.inject.Inject


class WebRTCClient @Inject constructor(
    private val context: Context,
    private val gson: Gson,
    private val webRTCManager: WebRTCManager
) {
    private var peerConnectionList = mutableMapOf<String, PeerConnection?>()
    private lateinit var webRTCListener: WebRTCListener
    private lateinit var localVideoTrack: VideoTrack
    private lateinit var localStream: MediaStream

    private val localStreamId = "local_stream"
    private val mediaConstraint = WebRTCUtils.getMediaConstraints()
    private val iceServers = WebRTCUtils.getIceServers()

    private val screen by lazy { context.resources.displayMetrics }

    fun createPeerConnection(webRTCListener: WebRTCListener, clientId: String, deviceId: String) {
        this.webRTCListener = webRTCListener

        val peerConnection = webRTCManager.createPeerConnection(iceServers, object : MyPeerObserver() {
            override fun onIceCandidate(cadidate: IceCandidate?) {
                super.onIceCandidate(cadidate)
                cadidate?.let { sendIceCandidate(it, clientId, deviceId) }
            }

            override fun onDataChannel(dataChannel: DataChannel?) {
                super.onDataChannel(dataChannel)
                dataChannel?.registerObserver(object : DataChannel.Observer {
                    override fun onBufferedAmountChange(p0: Long) {
                    }

                    override fun onStateChange() {
                        Log.d(ScreenCaptureForegroundService.TAG, "onStateChange: ${dataChannel.state()}")
                        if (dataChannel.state() == DataChannel.State.OPEN) {
                            ScreenRefresher(context).flashScreen()
                        }
                    }

                    override fun onMessage(p0: DataChannel.Buffer?) {
                        p0?.let {
                            val message = convertBufferToString(p0)

                            val command = gson.fromJson(message, GestureCommand::class.java)
                            val normalizedCommand = AspectRatioUtils.normalizeServerCoordinate(
                                screen.widthPixels,
                                screen.heightPixels,
                                command
                            )
                            DeviceControlService.getInstance()?.performGesture(normalizedCommand)
                        }
                    }
                })
            }
        })
        Log.d(TAG, "Peer Connection Created")

        localStream.addTrack(localVideoTrack)
        peerConnection?.addTrack(localVideoTrack, listOf(localStreamId))

        peerConnection?.setVideoBitrate(1_500_000, 8_000_000)

        peerConnectionList[clientId] = peerConnection
    }

    private fun convertBufferToString(buffer: DataChannel.Buffer): String {
        val bytes = ByteArray(buffer.data.remaining())
        buffer.data.get(bytes)
        return String(bytes, StandardCharsets.UTF_8)
    }

    fun startScreenCapturing(context:Context, intent: Intent) {
        val videoSource = webRTCManager.createVideoSource()
        val screenCaptureManager = ScreenCaptureManager(context, intent, videoSource)

        screenCaptureManager.startScreenCapturing()

        localVideoTrack = webRTCManager.createVideoTrack(videoSource)
        localStream = webRTCManager.getMyPeerConnectionFactory().createLocalMediaStream(localStreamId)
    }

    /**
     * This function is exclusively used by the server app.
     * It processes the received offer from the client app to establish a WebRTC connection.
     * The function performs the following steps:
     * 1. Creates an Answer response based on the received Offer.
     * 2. Sets the Answer as the localDescription on the peerConnection.
     * 3. Sends the generated Answer back to the client app via a socket connection.
     */
    fun createAnswer(clientId: String) {
        val peerConnection = peerConnectionList[clientId]
        peerConnection?.createAnswer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        webRTCListener.onTransferEventToSocket(
                            DataModelType.Answer,
                            DataModel(
                                username = clientId,
                                sdp = desc?.description
                            )
                        )
                        Log.d(TAG, "Answer sent to $clientId")
                    }
                }, desc)
            }

            override fun onCreateFailure(p0: String?) {
                super.onCreateFailure(p0)
                Log.d(TAG, "onCreateFailure >>>> $p0")
            }
        }, mediaConstraint)
    }

    private fun PeerConnection.setVideoBitrate(minBitrateBps: Int, maxBitrateBps: Int) {
        val senders = senders
        senders.forEach { sender ->
            val parameters = sender.parameters

            // Set bitrate for each encoding
            parameters.encodings.forEach { encoding ->
                encoding.minBitrateBps = minBitrateBps
                encoding.maxBitrateBps = maxBitrateBps
            }

            // Apply the parameters
            sender.parameters = parameters
        }
    }

    /**
     * Handles the received OFFER or ANSWER from the socket.
     * Adds the received session description to the PeerConnection.
     */

    fun setRemoteDescription(target: String, sdp: SessionDescription) {
        val peerConnection = peerConnectionList[target]
        peerConnection?.setRemoteDescription(MySdpObserver(), sdp)
    }

    /**
     * When we receive ICE candidate from client/server than
     * add this to the peerConnection
     */
    fun addIceCandidate(target: String, iceCandidate: IceCandidate) {
        val peerConnection = peerConnectionList[target]
        if (peerConnection?.remoteDescription != null) {
            peerConnection.addIceCandidate(iceCandidate)
        }else{
            Log.e(TAG, "Please wait for remoteDescription before adding IceCandidate")
        }
    }

    /**
     * Send the ICE candidate received from webRTC to target user
     */
    fun sendIceCandidate(candidate: IceCandidate, clientId: String, deviceId: String) {
        webRTCListener.onTransferEventToSocket(
            DataModelType.IceCandidate,
            DataModel(
                username = clientId,
                target = deviceId,
                iceCandidate = gson.toJson(candidate),
                isEmulator = true
            )
        )
    }

    private fun PeerConnection.removeStreamsTracks() {
        senders.forEach { removeTrack(it) }
    }

    fun disposePeerConnection(target: String) {
        val peerConnection = peerConnectionList[target]
        peerConnection?.apply {
            removeStreamsTracks()
            close()
            dispose()
        }
        peerConnectionList[target] = null
        println(peerConnectionList)
    }

    fun disposeServer() {
        peerConnectionList.forEach { (target, _) ->
            disposePeerConnection(target)
        }
    }

    companion object {
        const val TAG = "DAMARU"
    }
}