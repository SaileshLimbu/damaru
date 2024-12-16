package com.powersoft.damaruserver.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
import com.powersoft.common.model.GestureCommand
import com.powersoft.common.socket.SocketClient
import com.powersoft.common.socket.SocketListener
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

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
        startServiceWithNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && ACTION_START_CAPTURE == intent.action) {
            val data = intent.getParcelableExtra<Intent>(EXTRA_RESULT_DATA)
            if (data != null) {
//                @SuppressLint("HardwareIds")
//                val user: String = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
                val user = "test-emulator"

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

                            dataChannel?.registerObserver(object : DataChannel.Observer{
                                override fun onBufferedAmountChange(p0: Long) {
                                }

                                override fun onStateChange() {
                                }

                                override fun onMessage(p0: DataChannel.Buffer?) {
                                    p0.let {
                                        val bytes = ByteArray(p0!!.data.remaining())
                                        p0.data.get(bytes)
                                        val message = String(bytes, StandardCharsets.UTF_8)

                                        val command = gson.fromJson(message, GestureCommand::class.java)
                                        Log.d(TAG, "onDataChannelMessage: $command")
                                        DeviceControlService.getInstance()?.performGesture(command)
                                    }
                                }

                            })
                        }
                    })
            }
        }
        return START_STICKY
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
                webRTCClient.disposeServer()
            }

            DataModelType.Offer -> {
                //After receiving offer create and answer and send them
                Log.d(TAG, "onNewMessageReceived: OFFER received from " + model.username)
                this.targetUser = model.username
                webRTCClient.setTargetUser(model.username)
                webRTCClient.onRemoteSessionReceived(SessionDescription(SessionDescription.Type.OFFER, model.data.toString()))
                webRTCClient.createAnswer(model.username)
                webRTCClient.startScreenCapturing()
            }

            DataModelType.IceCandidates -> {
                val candidate = gson.fromJson(model.data.toString(), IceCandidate::class.java)
                webRTCClient.addIceCandidate(candidate)
            }

            else -> Unit
        }
    }

    override fun onTransferEventToSocket(data: DataModel) {
        socketClient.sendMessageToSocket(data)
    }

    override fun onChannelMessage(message: String) {
        try {
            val command = gson.fromJson(message, GestureCommand::class.java)
            DeviceControlService.getInstance()?.performGesture(command)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val TAG = "DAMARU_SERVER"
        const val ACTION_START_CAPTURE = "ACTION_START_CAPTURE"
        const val EXTRA_RESULT_DATA = "EXTRA_RESULT_DATA"
        private const val CHANNEL_ID = "ScreenCaptureServiceChannel"
    }
}
