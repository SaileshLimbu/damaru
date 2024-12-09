package com.d1vivek.projectz.service;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.d1vivek.projectz.R
import com.d1vivek.projectz.webrtc.WebrtcClient
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

@AndroidEntryPoint
class ShareService @Inject constructor() : Service() {


    companion object {
        var webrtcClient : WebrtcClient? = null
        var  surfaceView: SurfaceViewRenderer? = null
    }

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(
            NotificationManager::class.java
        )
        startServiceWithNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        webrtcClient?.startScreenCapturing(surfaceView!!)
        return START_STICKY
    }

    private fun stopMyService(){
        stopSelf()
        notificationManager.cancelAll()
    }

    private fun startServiceWithNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                "channel1","foreground",NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
            val notification = NotificationCompat.Builder(this,"channel1")
                .setSmallIcon(R.mipmap.ic_launcher)

            startForeground(1,notification.build())
        }

    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}