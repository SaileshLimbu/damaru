package com.powersoft.damaruserver.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
import com.powersoft.common.model.GestureCommand
import com.powersoft.common.socket.SocketClient
import com.powersoft.common.socket.SocketListener
import com.powersoft.common.utils.AspectRatioUtils
import com.powersoft.common.webrtc.MyPeerObserver
import com.powersoft.common.webrtc.WebRTCClient
import com.powersoft.common.webrtc.WebRTCListener
import com.powersoft.damaruserver.R
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@AndroidEntryPoint
class ScreenCaptureForegroundService : Service(), SocketListener, WebRTCListener {

    @Inject
    lateinit var socketClient: SocketClient

    @Inject
    lateinit var webRTCClient: WebRTCClient

    @Inject
    lateinit var gson: Gson

    private lateinit var notificationManager: NotificationManager
    private lateinit var targetUser: String
//    @SuppressLint("HardwareIds")
//    val user: String = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    private val user = "test-emulator"

    private lateinit var screen: DisplayMetrics

    override fun onCreate() {
        super.onCreate()
        screen = resources.displayMetrics
        notificationManager = getSystemService(NotificationManager::class.java)
        startServiceWithNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && ACTION_START_CAPTURE == intent.action) {
            val data = intent.getParcelableExtra<Intent>(EXTRA_RESULT_DATA)
            if (data != null) {
                socketClient.init(user, this)
                webRTCClient.init(
                    webRTCListener = this,
                    username = user,
                    intent = data,
                    observer = object : MyPeerObserver() {
                        override fun onIceCandidate(cadidate: IceCandidate?) {
                            super.onIceCandidate(cadidate)
                            cadidate?.let { webRTCClient.sendIceCandidate(it, targetUser) }
                        }

                        override fun onDataChannel(dataChannel: DataChannel?) {
                            super.onDataChannel(dataChannel)

                            dataChannel?.registerObserver(object : DataChannel.Observer {
                                override fun onBufferedAmountChange(p0: Long) {
                                }

                                override fun onStateChange() {
                                    Log.d(TAG, "onStateChange: ${dataChannel.state()}")
                                    if (dataChannel.state() == DataChannel.State.OPEN){
                                        DeviceControlService.getInstance()?.refreshScreen()
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
            }
        }
        return START_STICKY
    }

    private fun convertBufferToString(buffer: DataChannel.Buffer): String {
        val bytes = ByteArray(buffer.data.remaining())
        buffer.data.get(bytes)
        return String(bytes, StandardCharsets.UTF_8)
    }

    override fun onDestroy() {
        webRTCClient.disposeServer()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startServiceWithNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, "foreground", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Damaru Server")
                .setContentText("You are using damaru emulator.")
                .setSmallIcon(R.mipmap.ic_launcher)

            startForeground(1, notification.build())
        }
    }

    override fun onNewMessageReceived(model: DataModel) {
        when (model.type) {
            DataModelType.EndCall -> {
                Log.d(TAG, "Connected ended by ${model.username}")
            }

            DataModelType.Offer -> {
                //After receiving offer create and answer and send them
                Log.d(TAG, "onNewMessageReceived: OFFER received from " + model.username)
                this.targetUser = model.username
                webRTCClient.setTargetUser(model.username)
                webRTCClient.onRemoteSessionReceived(SessionDescription(SessionDescription.Type.OFFER, model.data.toString()))
                webRTCClient.createAnswer(model.username)
            }

            DataModelType.IceCandidates -> {
                val candidate = gson.fromJson(model.data.toString(), IceCandidate::class.java)
                webRTCClient.addIceCandidate(candidate)
            }

            else -> Unit
        }
    }

    override fun onWebSocketConnected() {
    }

    override fun onDataChannelConnected() {

    }

    override fun onTransferEventToSocket(data: DataModel) {
        socketClient.sendMessageToSocket(data)
    }

    override fun onChannelMessage(message: String) {
    }

    companion object {
        const val TAG = "DAMARU_SERVER"
        const val ACTION_START_CAPTURE = "ACTION_START_CAPTURE"
        const val EXTRA_RESULT_DATA = "EXTRA_RESULT_DATA"
        private const val CHANNEL_ID = "ScreenCaptureServiceChannel"
    }
}
