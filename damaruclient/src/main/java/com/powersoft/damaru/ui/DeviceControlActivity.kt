package com.powersoft.damaru.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
import com.powersoft.common.model.GestureAction
import com.powersoft.common.model.GestureCommand
import com.powersoft.common.socket.SocketClient
import com.powersoft.common.socket.SocketListener
import com.powersoft.common.utils.AlertUtils
import com.powersoft.common.utils.AspectRatioUtils
import com.powersoft.common.utils.DraggableTouchListener
import com.powersoft.common.utils.GestureDetector
import com.powersoft.common.utils.hide
import com.powersoft.common.utils.show
import com.powersoft.common.webrtc.MyPeerObserver
import com.powersoft.common.webrtc.WebRTCClient
import com.powersoft.common.webrtc.WebRTCListener
import com.powersoft.damaru.databinding.ActivityDeviceControlBinding
import com.powersoft.damaru.viewmodels.DeviceControlViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.IceCandidate
import org.webrtc.RendererCommon
import org.webrtc.RtpTransceiver
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack
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
        const val TAG = "DAMARU"
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

        binding.surfaceView.apply {
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
            setEnableHardwareScaler(true)
        }

        init()

        binding.btnDisconnect.setOnTouchListener(
            DraggableTouchListener {
                AlertUtils.showConfirmDialog(
                    this@DeviceControlActivity,
                    "Disconnect",
                    "Are you sure you want to disconnect?",
                    "Ok",
                    "Cancel"
                ) {
                    dispose()
                    finish()
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        binding.surfaceView.init(webrtcClient.getEglBase().eglBaseContext, null)
        sendCommand(GestureCommand(GestureAction.FLASH))
    }

    override fun onPause() {
        super.onPause()
        binding.surfaceView.release()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRemoteControl() {
        val screen = resources.displayMetrics
        val screenWidth = screen.widthPixels
        val screenHeight = screen.heightPixels

        binding.surfaceView.setOnTouchListener { _, event ->
            val x: Float = event.x
            val y: Float = event.y

            val normalizedCommand = AspectRatioUtils.normalizeControllerCoordinates(screenWidth, screenHeight,
                GestureCommand(GestureAction.PINCH_ZOOM, startX = x, startY = y),
                screenHeight - binding.surfaceView.height
                )

            sendCommand(GestureCommand(GestureAction.PINCH_ZOOM, event.action, startX = normalizedCommand.startX, startY = normalizedCommand.startY))
            true
        }

        binding.apply {
            navBack.setOnClickListener {
                val command = GestureCommand(GestureAction.BACK)
                sendCommand(command)
            }
            navHome.setOnClickListener {
                val command = GestureCommand(GestureAction.HOME)
                sendCommand(command)
            }
            navRecent.setOnClickListener {
                val command = GestureCommand(GestureAction.RECENT)
                sendCommand(command)
            }
        }
    }

    private fun sendCommand(command: GestureCommand) {
        webrtcClient.dataChannel?.let { webrtcClient.sendDataMessage(it, Gson().toJson(command)) }
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

                override fun onTrack(transceiver: RtpTransceiver?) {
                    super.onTrack(transceiver)
                    val remoteTrack = transceiver?.receiver?.track() as? VideoTrack
                    Log.d("damaru", "onAddTrack: $remoteTrack")
                    remoteTrack?.let {
                        runOnUiThread {
                            it.addSink(binding.surfaceView)
                        }
                    }
                }
            })
    }


    override fun onNewMessageReceived(type: DataModelType, model: DataModel) {
        when (type) {
            DataModelType.Answer -> {
                Log.d(TAG, "Answer received from ${model.target}")
                webrtcClient.onRemoteSessionReceived(SessionDescription(SessionDescription.Type.ANSWER, model.sdp.toString()))
            }

            DataModelType.IceCandidate -> {
                val candidate = gson.fromJson(model.iceCandidate.toString(), IceCandidate::class.java)
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
            binding.viewConnecting.hide()
            binding.viewRemoteDevice.show()
            initRemoteControl()
        }
        socketClient.sendMessageToSocket(DataModelType.Connect, DataModel(viewModel.clientId, viewModel.deviceId))
    }

    override fun onTransferEventToSocket(type: DataModelType, data: DataModel) {
        socketClient.sendMessageToSocket(type, data)
    }

    override fun onChannelMessage(message: String) {
        // 2 way comm
    }

    private fun dispose() {
        webrtcClient.disposeClient()
        binding.surfaceView.clearImage()
        binding.surfaceView.release()
        webrtcClient.closeConnection(viewModel.clientId, viewModel.deviceId)
        socketClient.closeSocket()
    }

    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }
}