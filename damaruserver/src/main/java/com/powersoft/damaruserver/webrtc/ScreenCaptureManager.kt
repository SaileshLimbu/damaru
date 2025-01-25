package com.powersoft.damaruserver.webrtc

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.util.Log
import org.webrtc.EglBase
import org.webrtc.ScreenCapturerAndroid
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoSource

class ScreenCaptureManager(
    private val context: Context,
    private val intent: Intent,
    private val videoSource: VideoSource
) {

    private companion object {
        private const val TAG = "ScreenCaptureManager"
    }

    private var screenCapturer: ScreenCapturerAndroid? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    fun startScreenCapturing() {
        surfaceTextureHelper = SurfaceTextureHelper.create(
            Thread.currentThread().name,
            EglBase.create(null, EglBase.CONFIG_PLAIN).eglBaseContext
        )

        screenCapturer = ScreenCapturerAndroid(intent, object : MediaProjection.Callback() {})

        try {
            screenCapturer?.initialize(
                surfaceTextureHelper!!, context, videoSource.capturerObserver
            )
            screenCapturer?.startCapture(
                context.resources.displayMetrics.widthPixels,
                context.resources.displayMetrics.heightPixels,
                60
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error starting screen capture: ${e.message}", e)
        }
    }

    fun stopCapture() {
        screenCapturer?.stopCapture()
        screenCapturer?.dispose()
        screenCapturer = null

        surfaceTextureHelper?.dispose()
        surfaceTextureHelper = null
    }
}
