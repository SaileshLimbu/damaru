package com.d1vivek.projectz.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
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
import org.json.JSONObject
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import javax.inject.Inject


@AndroidEntryPoint
class DeviceShareActivity : AppCompatActivity(), SocketClient.Listener, WebrtcClient.Listener {
    @Inject
    lateinit var socketClient: SocketClient

    @Inject
    lateinit var webrtcClient: WebrtcClient

    @Inject
    lateinit var gson: Gson

    private lateinit var screenCaptureLauncher: ActivityResultLauncher<Intent>
    private var username: String? = null
    private var targetUsername: String? = null

    private lateinit var b: ActivityDeviceShareBinding

    companion object {
        const val USER_NAME = "username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityDeviceShareBinding.inflate(layoutInflater)
        setContentView(b.root)

        init()
        b.btnCheck.setOnClickListener {
            Toast.makeText(applicationContext, "fuck dipesh", Toast.LENGTH_SHORT).show()
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
            webrtcClient.initializeWebrtcClient(username!!, SurfaceViewRenderer(this),
                object : MyPeerObserver() {
                    override fun onIceCandidate(p0: IceCandidate?) {
                        super.onIceCandidate(p0)
                        p0?.let { webrtcClient.sendIceCandidate(it, targetUsername!!) }
                        Log.d("damaru", "onIceCandidate: ${p0.toString()}")
                    }

                    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                        super.onConnectionChange(newState)
                        Log.d("damaru", "onConnectionChange: $newState")
//                        if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
//                            webrtcClient.createDataChannel()
//                        }
                    }

                    override fun onAddStream(p0: MediaStream?) {
                        super.onAddStream(p0)
                        Log.d("damaru", "onAddStream: $p0")
                    }

                    override fun onDataChannel(p0: DataChannel?) {
                        super.onDataChannel(p0)
                        Log.d("damaru", "onDataChannel: $p0")

                        p0?.registerObserver(object : DataChannel.Observer {
                            override fun onBufferedAmountChange(p0: Long) {
                            }

                            override fun onStateChange() {
                            }

                            override fun onMessage(p0: DataChannel.Buffer?) {
                                Log.d("damaru", "onDataChannel onMessage: ${p0.toString()}")
                            }
                        })
                    }
                })

            webrtcClient.createDataChannel()
            //start the service right away
            if (ShareService.webrtcClient == null) {
                ShareService.webrtcClient = webrtcClient
                ShareService.surfaceView = b.surfaceView
                val startIntent = Intent(this@DeviceShareActivity, ShareService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(startIntent)
                } else {
                    startService(startIntent)
                }
            }
        } else {
            Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun endScreenShare() {
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
        when (model.type) {
            DataModelType.StartStreaming -> {
                targetUsername = model.username
                webrtcClient.call(targetUsername!!)
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
                webrtcClient.answer(model.username)
            }

            DataModelType.Answer -> {
                webrtcClient.onRemoteSessionReceived(
                    SessionDescription(SessionDescription.Type.ANSWER, model.data.toString())
                )

                if (ShareService.webrtcClient == null) {
                    ShareService.webrtcClient = webrtcClient
                    ShareService.surfaceView = b.surfaceView
                    val startIntent = Intent(this@DeviceShareActivity, ShareService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(startIntent)
                    } else {
                        startService(startIntent)
                    }
                }
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
        socketClient.sendMessageToSocket(data)
    }

    override fun onChannelMessage(message: String) {
        Log.d("damaru", "onChannelMessage >>>>>>>>>>>>>> $message")
        dispatchTouches(message)

//        val json = JSONObject(message)
//        val x = json.getDouble("x")
//        val y = json.getDouble("y")
////        runCommand("adb shell input tap $x $y ;")
//        //runCommand(arrayOf("bash", "-l", "-c", "adb shell input tap $x $y ;"))
    }

    private fun dispatchTouches(eventJson: String) {
        val json = JSONObject(eventJson)
        // Obtain MotionEvent object
        val downTime = SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis() + 100
        val x = json.getDouble("x")
        val y = json.getDouble("y")
        val action = json.getInt("action")
        val metaState = json.getInt("metaState")

        val motionEvent = MotionEvent.obtain(
            downTime,
            eventTime,
            action,
            x.toFloat(),
            y.toFloat(),
            metaState
        )

        window.superDispatchTouchEvent(motionEvent)

        // Dispatch touch event to view
        b.root.post {
            Log.d("damaru", "button pos >> ${b.btnCheck.x} : ${b.btnCheck.y}")
            b.root.dispatchTouchEvent(motionEvent)
        }
    }

    fun runCommand(command: Array<String>) {
        // To avoid UI freezes run in thread
        Thread {
            var out: OutputStream? = null
            var `in`: InputStream? = null
            try {
                // Send script into runtime process
                val child = Runtime.getRuntime().exec(command)
                // Get input and output streams
                out = child.outputStream
                `in` = child.inputStream
                //Input stream can return anything
                val bufferedReader = BufferedReader(InputStreamReader(`in`))
                var line: String?
                var result: String? = ""
                while ((bufferedReader.readLine().also { line = it }) != null) result += line
                //Handle input stream returned message
                handleBashCommandsResult(result)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (`in` != null) {
                    try {
                        `in`.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                if (out != null) {
                    try {
                        out.flush()
                        out.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }

    private fun handleBashCommandsResult(result: String?) {
        Log.d("damary", "handleBashCommandResult >>> $result")
    }

}