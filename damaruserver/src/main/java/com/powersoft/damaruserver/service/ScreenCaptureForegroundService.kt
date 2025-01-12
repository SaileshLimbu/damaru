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
import com.powersoft.common.socket.SocketClient
import com.powersoft.common.socket.SocketListener
import com.powersoft.common.webrtc.WebRTCListener
import com.powersoft.damaruserver.R
import com.powersoft.damaruserver.utils.DeviceUtils
import com.powersoft.damaruserver.webrtc.WebRTCClient
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.IceCandidate
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

    private lateinit var deviceId: String

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
        startServiceWithNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && ACTION_START_CAPTURE == intent.action) {
            val data = intent.getParcelableExtra<Intent>(EXTRA_RESULT_DATA)
            deviceId = intent.getStringExtra(EXTRA_DEVICE_ID) ?: DeviceUtils.getDeviceId(this)
            if (data != null) {
                socketClient.init(deviceId, this, getString(R.string.token), true)
                webRTCClient.startScreenCapturing(this, data)
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

    override fun onNewMessageReceived(type: DataModelType, model: DataModel) {
        when (type) {
            DataModelType.Disconnect -> {
                Log.d(TAG, "Connection ended by ${model.username}")
                model.username?.let { webRTCClient.disposePeerConnection(it) }
            }

            DataModelType.Offer -> {
                model.username?.let {
                    //After receiving offer create and answer and send them
                    Log.d(TAG, "onNewMessageReceived: OFFER received from $it")
                    webRTCClient.createPeerConnection(this, clientId = it, deviceId)
                    webRTCClient.setRemoteDescription(it, SessionDescription(SessionDescription.Type.OFFER, model.sdp.toString()))
                    webRTCClient.createAnswer(clientId = it)
                }
            }

            DataModelType.IceCandidate -> {
                val candidate = gson.fromJson(model.iceCandidate.toString(), IceCandidate::class.java)
                model.username?.let { webRTCClient.addIceCandidate(it, candidate) }
            }

            else -> Unit
        }
    }

    override fun onSocketConnected() {
    }

    override fun onDataChannelConnected() {

    }

    override fun onTransferEventToSocket(type: DataModelType, data: DataModel) {
        socketClient.sendMessageToSocket(type, data)
    }

    override fun onChannelMessage(message: String) {
    }

    companion object {
        const val TAG = "DAMARU_SERVER"
        const val ACTION_START_CAPTURE = "ACTION_START_CAPTURE"
        const val EXTRA_RESULT_DATA = "EXTRA_RESULT_DATA"
        const val EXTRA_DEVICE_ID = "EXTRA_DEVICE_ID"
        private const val CHANNEL_ID = "ScreenCaptureServiceChannel"
    }
}
