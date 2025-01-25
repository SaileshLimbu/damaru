package com.powersoft.damaruserver.webrtc

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
import com.powersoft.common.model.GestureAction
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
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.VideoSource
import org.webrtc.VideoTrack
import java.nio.charset.StandardCharsets
import java.util.Collections
import javax.inject.Inject

class WebRTCClient @Inject constructor(
    private val context: Context,
    private val gson: Gson,
    private val webRTCManager: WebRTCManager
) {
    private var peerConnectionList = Collections.synchronizedMap(mutableMapOf<String, PeerConnection?>())
    private var webRTCListener: WebRTCListener? = null
    private var localVideoTrack: VideoTrack? = null
    private var localStream: MediaStream? = null
    private var screenCaptureManager: ScreenCaptureManager? = null
    private var videoSource: VideoSource? = null

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
                            if (command.action == GestureAction.FLASH) {
                                ScreenRefresher(context).flashScreen()
                            } else {
                                val normalizedCommand = AspectRatioUtils.normalizeServerCoordinate(
                                    screen.widthPixels,
                                    screen.heightPixels,
                                    command
                                )
                                DeviceControlService.getInstance()?.performGesture(normalizedCommand)
                            }
                        }
                    }
                })
            }
        })
        Log.d(TAG, "Peer Connection Created")

        localStream?.addTrack(localVideoTrack)
        peerConnection?.addTrack(localVideoTrack, listOf(localStreamId))

        peerConnection?.setVideoBitrate(MIN_BITRATE, MAX_BITRATE)

        peerConnectionList[clientId] = peerConnection
    }

    private fun convertBufferToString(buffer: DataChannel.Buffer): String {
        val bytes = ByteArray(buffer.data.remaining())
        buffer.data.get(bytes)
        return String(bytes, StandardCharsets.UTF_8)
    }

    fun startScreenCapturing(context: Context, intent: Intent) {
        videoSource = webRTCManager.createVideoSource()
        screenCaptureManager = ScreenCaptureManager(context, intent, videoSource!!)

        screenCaptureManager?.startScreenCapturing()

        localVideoTrack = webRTCManager.createVideoTrack(videoSource!!)
        localStream = webRTCManager.getMyPeerConnectionFactory().createLocalMediaStream(localStreamId)
    }

    fun createAnswer(clientId: String) {
        val peerConnection = peerConnectionList[clientId]
        peerConnection?.createAnswer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        webRTCListener?.onTransferEventToSocket(
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
        } else {
            Log.e(TAG, "Please wait for remoteDescription before adding IceCandidate")
        }
    }

    /**
     * Send the ICE candidate received from webRTC to target user
     */
    fun sendIceCandidate(candidate: IceCandidate, clientId: String, deviceId: String) {
        webRTCListener?.onTransferEventToSocket(
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

    /**
     * @return if we still have active connections
     */
    fun disposePeerConnection(target: String) : Boolean {
        peerConnectionList[target]?.apply {
            removeStreamsTracks()
            close()
            dispose()
        }
        peerConnectionList.remove(target)

        if (peerConnectionList.isEmpty()){
            screenCaptureManager?.stopCapture()
            screenCaptureManager = null
            localStream?.removeTrack(localVideoTrack)
            videoSource?.dispose()
            videoSource = null
            localStream?.dispose()
            localStream = null
            localVideoTrack?.dispose()
            localVideoTrack = null
        }

        return peerConnectionList.isNotEmpty()
    }

    fun disposeServer() {
        peerConnectionList.forEach { (target, _) ->
            disposePeerConnection(target)
        }
    }

    companion object {
        private const val TAG = "WebRTCClient"
        private const val MIN_BITRATE = 200_000
        private const val MAX_BITRATE = 2_000_000
    }
}