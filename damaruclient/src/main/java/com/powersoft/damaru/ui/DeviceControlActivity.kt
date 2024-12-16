package com.powersoft.damaru.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.powersoft.damaru.databinding.ActivityDeviceControlBinding
import com.powersoft.damaru.utils.GestureDetector
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
import com.powersoft.common.model.GestureAction
import com.powersoft.common.model.GestureCommand
import com.powersoft.common.socket.SocketClient
import com.powersoft.common.socket.SocketListener
import com.powersoft.common.webrtc.MyPeerObserver
import com.powersoft.common.webrtc.WebRTCClient
import com.powersoft.common.webrtc.WebRTCListener
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.RendererCommon
import org.webrtc.RtpReceiver
import org.webrtc.SessionDescription
import javax.inject.Inject

@AndroidEntryPoint
class DeviceControlActivity : AppCompatActivity(), SocketListener, WebRTCListener {

    @Inject
    lateinit var socketClient: SocketClient

    @Inject
    lateinit var webrtcClient: WebRTCClient

    @Inject
    lateinit var gson: Gson

    private lateinit var binding: ActivityDeviceControlBinding
    private lateinit var gestureDetector: GestureDetector
    private var username: String? = null
    private var targetUsername: String? = null

    companion object {
        const val USER_NAME = "username"
        const val TARGET_USER_NAME = "targetUser"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeviceControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        username = intent.getStringExtra(USER_NAME)
        targetUsername = intent.getStringExtra(TARGET_USER_NAME)

        binding.surfaceView.apply {
            init(webrtcClient.getEglBase().eglBaseContext, null)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
            setEnableHardwareScaler(true)
        }

        init()
        initRemoteControl()

        binding.btnDisconnect.setOnClickListener {
            binding.surfaceView.release()
            webrtcClient.closeConnection(targetUsername!!)
            socketClient.closeSocket()
            finish()
        }

        binding.btnReconnect.setOnClickListener {
            webrtcClient.sendOffer(targetUsername!!)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRemoteControl() {
        gestureDetector = GestureDetector { command ->
            webrtcClient.dataChannel?.let { webrtcClient.sendDataMessage(it, Gson().toJson(command)) }
        }
        binding.surfaceView.setOnTouchListener { view, event -> gestureDetector.onTouch(view, event) }
        binding.apply {
            navBack.setOnClickListener {
                val command = GestureCommand(GestureAction.BACK)
                webrtcClient.dataChannel?.let { webrtcClient.sendDataMessage(it, Gson().toJson(command)) }
            }
            navHome.setOnClickListener {
                val command = GestureCommand(GestureAction.HOME)
                webrtcClient.dataChannel?.let { webrtcClient.sendDataMessage(it, Gson().toJson(command)) }
            }
            navRecent.setOnClickListener {
                val command = GestureCommand(GestureAction.RECENT)
                webrtcClient.dataChannel?.let { webrtcClient.sendDataMessage(it, Gson().toJson(command)) }
            }
        }
    }

    private fun init() {
        socketClient.init(username!!, this)

        webrtcClient.init(
            webRTCListener = this,
            username = username!!,
            observer = object : MyPeerObserver() {
                override fun onIceCandidate(cadidate: IceCandidate?) {
                    super.onIceCandidate(cadidate)
                    cadidate?.let { webrtcClient.sendIceCandidate(it, targetUsername!!) }
                    Log.d("damaru", "onIceCandidate: ${cadidate.toString()}")
                }

                override fun onAddStream(stream: MediaStream?) {
                    super.onAddStream(stream)
                    Log.d("damaru", "onAddStream: $stream")
                    stream?.videoTracks?.get(0)?.addSink(binding.surfaceView)
                    Log.e("damaru", "onAddStream ${stream?.videoTracks?.get(0)}")
                }

                override fun onAddTrack(rtpReceiver: RtpReceiver?, streamArr: Array<out MediaStream>?) {
                    super.onAddTrack(rtpReceiver, streamArr)
                    Log.d("damaru", "onAddTrack: $streamArr")
                    rtpReceiver?.SetObserver {
                        Log.e("damaru", "rtpReceiver $it")
                    }
                    streamArr?.get(0)?.videoTracks?.get(0)?.addSink(binding.surfaceView)
                    Log.e("damaru", "onAddStream ${streamArr?.get(0)?.videoTracks?.get(0)}")
                }

                @SuppressLint("ClickableViewAccessibility")
                override fun onDataChannel(dataChannel: DataChannel?) {
                    super.onDataChannel(dataChannel)
                    Log.d("damaru", "onDataChannel: $dataChannel")
                    dataChannel?.registerObserver(object : DataChannel.Observer {
                        override fun onBufferedAmountChange(p0: Long) {
                        }

                        override fun onStateChange() {
                        }

                        override fun onMessage(p0: DataChannel.Buffer?) {
                            Log.d("damaru", "onDataChannel onMessage (From Client): ${p0.toString()}")
                        }
                    })
                }
            })

        webrtcClient.sendOffer(targetUsername!!)
    }

    override fun onNewMessageReceived(model: DataModel) {
        when (model.type) {
            DataModelType.Answer -> {
                webrtcClient.onRemoteSessionReceived(SessionDescription(SessionDescription.Type.ANSWER, model.data.toString()))
            }

            DataModelType.IceCandidates -> {
                val candidate = gson.fromJson(model.data.toString(), IceCandidate::class.java)
                webrtcClient.addIceCandidate(candidate)
            }

            else -> Unit
        }
    }

    override fun onTransferEventToSocket(data: DataModel) {
        socketClient.sendMessageToSocket(data)
    }

    override fun onChannelMessage(message: String) {
        // 2 way comm
    }
}