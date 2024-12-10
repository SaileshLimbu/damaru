package com.d1vivek.projectz.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.d1vivek.projectz.databinding.ActivityDeviceShareBinding
import com.d1vivek.projectz.service.ShareService
import com.d1vivek.projectz.socket.SocketClient
import com.d1vivek.projectz.utils.DataModel
import com.d1vivek.projectz.utils.DataModelType
import com.d1vivek.projectz.webrtc.MyPeerObserver
import com.d1vivek.projectz.webrtc.WebrtcClient
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

@AndroidEntryPoint
class DeviceShareActivity : AppCompatActivity(), SocketClient.Listener, WebrtcClient.Listener {
    @Inject lateinit var socketClient: SocketClient
    @Inject lateinit var webrtcClient: WebrtcClient
    @Inject lateinit var gson: Gson

    private lateinit var screenCaptureLauncher: ActivityResultLauncher<Intent>
    private var username: String? = null
    private var targetUsername: String? = null

    private lateinit var b : ActivityDeviceShareBinding

    companion object {
        const val USER_NAME = "username"
        const val TARGET_USER_NAME = "targetUser"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityDeviceShareBinding.inflate(layoutInflater)
        setContentView(b.root)
        init()

        targetUsername="theone"
        b.btnCheck.setOnClickListener {
            webrtcClient.call(targetUsername!!)
        }
    }

    private fun init() {
        username = intent.getStringExtra(USER_NAME)
        if (username.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "No Username", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        screenCaptureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleScreenCaptureResult(result.resultCode, result.data)
        }
        startScreenCapture()
    }

    private fun startScreenCapture() {
        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
        screenCaptureLauncher.launch(captureIntent)
    }

    private fun handleScreenCaptureResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            socketClient.listener = this
            socketClient.init(username!!)

            webrtcClient.listener = this
            webrtcClient.setPermissionIntent(data)
            webrtcClient.initializeWebrtcClient(username!!, b.surface,
                object : MyPeerObserver() {
                    override fun onIceCandidate(p0: IceCandidate?) {
                        super.onIceCandidate(p0)
                        p0?.let { webrtcClient.sendIceCandidate(it, targetUsername!!) }
                        Log.d("damaru", "onIceCandidate: ${p0.toString()}")
                    }

                    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                        super.onConnectionChange(newState)
                        Log.d("damaru", "onConnectionChange: $newState")
                        if (newState == PeerConnection.PeerConnectionState.CONNECTED){
                        }
                    }

                    override fun onAddStream(p0: MediaStream?) {
                        super.onAddStream(p0)
                        Log.d("damaru", "onAddStream: $p0")
                    }
                })
        } else {
            Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun endScreenShare(){
        socketClient.sendMessageToSocket(
            DataModel(
                type = DataModelType.EndCall,
                username = username!!,
                target = null,
                null
            )
        )
    }

    override fun onNewMessageReceived(model: DataModel) {
        Log.d("damaru", "onNewMessageReceived: $model")
        when (model.type) {
            DataModelType.StartStreaming -> {
            }
            DataModelType.EndCall -> {
                finish()
            }
            DataModelType.Offer -> {
                webrtcClient.onRemoteSessionReceived(
                    SessionDescription(
                        SessionDescription.Type.OFFER, model.data
                            .toString()
                    )
                )
//                targetUsername = model.username
                webrtcClient.answer(model.username)
            }
            DataModelType.Answer -> {
                //start streaming
//                ShareService.webrtcClient = webrtcClient
//                ShareService.surfaceView = b.surface
//                val startIntent = Intent(this@DeviceShareActivity, ShareService::class.java)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                    startForegroundService(startIntent)
//                } else {
//                    startService(startIntent)
//                }
            }
            DataModelType.IceCandidates -> {
                val candidate = try {
                    gson.fromJson(model.data.toString(), IceCandidate::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
                candidate?.let {
                    webrtcClient.addIceCandidate(it)
                }
            }
            else -> Unit
        }
    }

    override fun onTransferEventToSocket(data: DataModel) {
        Log.d("damaru", "onTransferEventToSocket: $data")
        socketClient.sendMessageToSocket(data)

        if(data.type == DataModelType.Answer){
            //start streaming
            ShareService.webrtcClient = webrtcClient
            ShareService.surfaceView = b.surface
            val startIntent = Intent(this@DeviceShareActivity, ShareService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                startForegroundService(startIntent)
            } else {
                startService(startIntent)
            }
        }
    }
}