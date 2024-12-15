package com.powersoft.damaruserver.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
import com.powersoft.common.socket.SocketClient
import com.powersoft.common.socket.SocketListener
import com.powersoft.common.webrtc.MyPeerObserver
import com.powersoft.damaruserver.R
import com.powersoft.common.webrtc.WebRTCClient
import com.powersoft.common.webrtc.WebRTCClient.Companion
import com.powersoft.common.webrtc.WebRTCListener
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
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
                @SuppressLint("HardwareIds")
                val user: String = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
                //Initialize WebSocket
                socketClient.init(user, this)

                //Initialize WebRTCClient
                webRTCClient.init(data, this, object : MyPeerObserver() {
                    override fun onIceCandidate(p0: IceCandidate?) {
                        super.onIceCandidate(p0)
                        p0?.let { webRTCClient.sendIceCandidate(it, targetUser) }
                    }

                    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                        super.onConnectionChange(newState)
                        Log.d(WebRTCClient.TAG, "onConnectionChange: $newState")
                    }

                    @SuppressLint("ClickableViewAccessibility")
                    override fun onDataChannel(p0: DataChannel?) {
                        super.onDataChannel(p0)
                        Log.d(WebRTCClient.TAG, "onDataChannel: $p0")

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
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopScreenCapture()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun stopScreenCapture() {
        stopForeground(true)
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
            DataModelType.StartStreaming -> {}

            DataModelType.EndCall -> {
                webRTCClient.closeConnection()
            }

            DataModelType.Offer -> {
                //After receiving offer create and answer and send them
                Log.d(TAG, "onNewMessageReceived: OFFER received from " + model.username)
                this.targetUser = model.username
                webRTCClient.setTargetUser(model.username)
                webRTCClient.onRemoteSessionReceived(SessionDescription(SessionDescription.Type.OFFER, model.data.toString()))
                webRTCClient.createAnswer(model.username)
            }

            DataModelType.Answer -> {}

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
        Log.d("DAMARU_SERVER", "onChannelMessage >>>>>>>>>>>>>> $message")
    }

    companion object {
        const val TAG = "DAMARU_SERVER"
        const val ACTION_START_CAPTURE = "ACTION_START_CAPTURE"
        const val EXTRA_RESULT_DATA = "EXTRA_RESULT_DATA"
        const val DEVICE_ID = "DEVICE_ID"
        private const val CHANNEL_ID = "ScreenCaptureServiceChannel"
    }
}
