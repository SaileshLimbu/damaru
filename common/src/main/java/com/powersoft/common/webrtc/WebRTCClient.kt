package com.powersoft.common.webrtc

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.Observer
import org.webrtc.SessionDescription
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class WebRTCClient @Inject constructor(
    context: Context
) {
    private val webRTCManager = WebRTCManager(context)
    private var peerConnection: PeerConnection? = null
    var dataChannel: DataChannel? = null
    private lateinit var webRTCListener: WebRTCListener
    private lateinit var username: String

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

    fun init(webRTCListener: WebRTCListener, username: String, observer: Observer) {
        this.webRTCListener = webRTCListener
        this.username = username

        peerConnection = webRTCManager.createPeerConnection(iceServers, observer)

        createDataChannel()
    }

    private fun createDataChannel() {
        dataChannel = peerConnection?.createDataChannel("dataChannel", DataChannel.Init())
        dataChannel?.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(p0: Long) {
                Log.d(TAG, "onBufferedAmountChange >> $p0")
            }

            override fun onStateChange() {
                Log.d(TAG, "onStateChange >> ${dataChannel?.state()}")
                if (dataChannel?.state() == DataChannel.State.OPEN) {
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

    fun getEglBase() = webRTCManager.getEglBase()

    /**
     * Used exclusively by the client app to initiate a WebRTC connection.
     *
     * This function creates an SDP (Session Description Protocol) offer using the PeerConnection.
     * Once the offer is successfully created, it is set as the local description.
     * After setting the local description, the offer is sent to the server via a socket
     * as part of the signaling process.
     */
    fun sendOffer(deviceId: String) {
        peerConnection?.createOffer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection?.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        webRTCListener.onTransferEventToSocket(
                            DataModelType.Offer,
                            DataModel(
                                target = deviceId,
                                sdp = desc?.description
                            )
                        )
                    }
                }, desc)
            }
        }, mediaConstraint)
    }

    fun onRemoteSessionReceived(sessionDescription: SessionDescription) {
        peerConnection?.setRemoteDescription(MySdpObserver(), sessionDescription)
    }

    /**
     * When we receive ICE candidate from client/server than
     * add this to the peerConnection
     */
    fun addIceCandidate(iceCandidate: IceCandidate) {
        Log.e(TAG, "Received from Server: $iceCandidate")
        peerConnection?.addIceCandidate(iceCandidate)
    }

    /**
     * Send the ICE candidate received from webRTC to target user
     */
    fun sendIceCandidate(candidate: IceCandidate, clientId: String, deviceId: String) {
        Log.d(TAG, "sendIceCandidate from Client: $candidate Device id : $deviceId")
        webRTCListener.onTransferEventToSocket(
            DataModelType.IceCandidate,
            DataModel(
                username = clientId,
                target = deviceId,
                iceCandidate = Gson().toJson(candidate)
            )
        )
    }

    /**
     * When client disconnects from their site. Close the connection
     * and do some clean up.
     */
    fun closeConnection(clientId: String, deviceId: String) {
        webRTCListener.onTransferEventToSocket(
            DataModelType.Disconnect,
            DataModel(
                username = clientId,
                target = deviceId
            )
        )
        disposeClient()
    }

    private fun PeerConnection.removeStreamsTracks() {
        senders.forEach { removeTrack(it) }
    }

    /**
     * When user is no longer connected to emulator then dispose
     */
    fun disposeClient() {
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