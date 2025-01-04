package com.powersoft.damaru.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
import com.powersoft.common.model.GestureAction
import com.powersoft.common.model.GestureCommand
import com.powersoft.common.socket.SocketClient
import com.powersoft.common.socket.SocketListener
import com.powersoft.common.utils.AspectRatioUtils
import com.powersoft.common.utils.GestureDetector
import com.powersoft.common.webrtc.MyPeerObserver
import com.powersoft.common.webrtc.WebRTCClient
import com.powersoft.common.webrtc.WebRTCListener
import com.powersoft.damaru.databinding.ActivityDeviceControlBinding
import com.powersoft.damaru.utils.DraggableTouchListener
import com.powersoft.damaru.viewmodels.DeviceControlViewModel
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

    private val viewModel by viewModels<DeviceControlViewModel>()

    @Inject
    lateinit var socketClient: SocketClient

    @Inject
    lateinit var webrtcClient: WebRTCClient

    @Inject
    lateinit var gson: Gson

    private lateinit var binding: ActivityDeviceControlBinding
    private lateinit var gestureDetector: GestureDetector

    companion object {
        const val CLIENT_ID = "username"
        const val DEVICE_ID = "targetUser"
        const val TOKEN = "token"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeviceControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra(CLIENT_ID)?.let { viewModel.clientId = it }
        intent.getStringExtra(DEVICE_ID)?.let { viewModel.deviceId = it }
        intent.getStringExtra(TOKEN)?.let { viewModel.token = it }

        Log.e("damaru", "onCreate: ${viewModel.clientId}", )

        binding.surfaceView.apply {
            init(webrtcClient.getEglBase().eglBaseContext, null)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
            setEnableHardwareScaler(true)
        }

        init()
        initRemoteControl()

        binding.btnDisconnect.setOnTouchListener(
            DraggableTouchListener {
                dispose()
                finish()
            }
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRemoteControl() {
        val screen = resources.displayMetrics
        val screenWidth = screen.widthPixels
        val screenHeight = screen.heightPixels
        gestureDetector = GestureDetector { command ->
            val normalizedCommand = AspectRatioUtils.normalizeControllerCoordinates(
                screenWidth,
                screenHeight,
                command,
                binding.surfaceView.y.toInt()
            )
            webrtcClient.dataChannel?.let { webrtcClient.sendDataMessage(it, Gson().toJson(normalizedCommand)) }
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

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        socketClient.init(viewModel.clientId, this, viewModel.token, false)

        webrtcClient.init(
            webRTCListener = this,
            username = viewModel.clientId,
            observer = object : MyPeerObserver() {
                override fun onIceCandidate(cadidate: IceCandidate?) {
                    super.onIceCandidate(cadidate)
                    cadidate?.let { webrtcClient.sendIceCandidate(it, viewModel.clientId, viewModel.deviceId) }
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
    }


    override fun onNewMessageReceived(type: DataModelType, model: DataModel) {
        when (type) {
            DataModelType.Answer -> {
                webrtcClient.onRemoteSessionReceived(SessionDescription(SessionDescription.Type.ANSWER, model.sdp.toString()))
            }

            DataModelType.IceCandidate -> {
                val candidate = gson.fromJson(model.iceCandidate.toString(), IceCandidate::class.java)
                Log.i("ICE_TAG", "Received from Server: $model")
                webrtcClient.addIceCandidate(candidate)
            }

            else -> Unit
        }
    }

    override fun onSocketConnected() {
        webrtcClient.sendOffer(viewModel.deviceId)
    }

    override fun onDataChannelConnected() {
        runOnUiThread {
            binding.viewConnecting.visibility = View.GONE
            binding.viewRemoteDevice.visibility = View.VISIBLE
        }
    }

    override fun onTransferEventToSocket(type: DataModelType, data: DataModel) {
        socketClient.sendMessageToSocket(type, data)
    }

    override fun onChannelMessage(message: String) {
        // 2 way comm
    }

    private fun dispose() {
        webrtcClient.disposeClient()
        binding.surfaceView.release()
        webrtcClient.closeConnection(viewModel.clientId, viewModel.deviceId)
        socketClient.closeSocket()
    }

    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }
}