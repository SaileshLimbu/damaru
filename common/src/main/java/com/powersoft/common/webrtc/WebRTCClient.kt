package com.powersoft.common.webrtc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.provider.Settings
import android.util.Log
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
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
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class WebRTCClient @Inject constructor(
    private val context: Context
) {
    private val webRTCManager = WebRTCManager(context)
    private var peerConnection: PeerConnection? = null
    private var dataChannel: DataChannel? = null
    private lateinit var listener: WebRTCListener

    //Damaru Server App
    private var screenCapturer: VideoCapturer? = null
    private var localStream: MediaStream? = null
    private lateinit var targetUser: String
    private lateinit var intent: Intent
    private val localStreamId = "local_stream"
    @SuppressLint("HardwareIds")
    private val username: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    private val mediaConstraint = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
    }

    fun setTargetUser(target: String){
        this.targetUser = target
    }

    fun init(intent: Intent, listener: WebRTCListener, observer: Observer) {
        this.listener = listener
        this.intent = intent

        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )

        peerConnection = webRTCManager.createPeerConnection(iceServers, observer)

        createDataChannel()

        startScreenCapturing(intent)
    }

    private fun createDataChannel() {
        dataChannel = peerConnection?.createDataChannel("dataChannel", DataChannel.Init())
        dataChannel?.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(p0: Long) {
                Log.d(TAG, "onBufferedAmountChange >> $p0")
            }

            override fun onStateChange() {
                Log.d(TAG, "onStateChange >> ${dataChannel?.state()}")
            }

            override fun onMessage(p0: DataChannel.Buffer?) {
                p0.let {
                    val bytes = ByteArray(p0!!.data.remaining())
                    p0.data.get(bytes)
                    val message = String(bytes, StandardCharsets.UTF_8)
                    listener?.onChannelMessage(message)
                }
            }
        })
        Log.d(TAG, "Data Channel Created !!! $dataChannel")
    }

    private fun startScreenCapturing(intent: Intent) {
        val metrics = context.resources.displayMetrics
        val densityDpi = metrics.densityDpi
        val screenWidthPixels = metrics.widthPixels
        val screenHeightPixels = metrics.heightPixels

        val surfaceTextureHelper = SurfaceTextureHelper.create(
            Thread.currentThread().name, webRTCManager.eglBase.eglBaseContext
        )

        screenCapturer = createScreenCapturer(intent)
        val videoSource = webRTCManager.createVideoSource()
        screenCapturer?.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
        screenCapturer?.startCapture(screenWidthPixels, screenHeightPixels, 30)

        val localVideoTrack = webRTCManager.createVideoTrack(videoSource)

        localStream = webRTCManager.getMyPeerConnectionFactory().createLocalMediaStream(localStreamId)
        localStream?.addTrack(localVideoTrack)
        if (localStream != null) {
            Log.d(TAG, "startScreenCapturing: ${localStream.toString()}")
            peerConnection?.addTrack(localVideoTrack, listOf(localStreamId))
        }
    }

    private fun createScreenCapturer(intent: Intent): VideoCapturer {
        return ScreenCapturerAndroid(intent, object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                Log.d("ScreenCaptureService", "onStop: stopped screen capture")
            }

            override fun onCapturedContentVisibilityChanged(isVisible: Boolean) {
                super.onCapturedContentVisibilityChanged(isVisible)
                Log.d("ScreenCaptureService", "Visibility changed: $isVisible")
            }
        })
    }

    fun createAnswer(target: String) {
        peerConnection!!.createAnswer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection?.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        listener?.onTransferEventToSocket(
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

    fun onRemoteSessionReceived(sessionDescription: SessionDescription) {
        peerConnection?.setRemoteDescription(MySdpObserver(), sessionDescription)
    }

    fun addIceCandidate(iceCandidate: IceCandidate) {
        Log.d(TAG, "onIceCandidateAdded (EMULATOR)($targetUser): $iceCandidate")
        peerConnection?.addIceCandidate(iceCandidate)
    }

    fun sendIceCandidate(candidate: IceCandidate, target: String) {
        listener?.onTransferEventToSocket(
            DataModel(
                type = DataModelType.IceCandidates,
                username = username,
                target = target,
                data = Gson().toJson(candidate)
            )
        )
    }

    fun closeConnection() {
        try {
            screenCapturer?.stopCapture()
            screenCapturer?.dispose()
            localStream?.dispose()
            peerConnection?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val TAG = "DAMARU_SERVER-WebRTCClient"
    }
}