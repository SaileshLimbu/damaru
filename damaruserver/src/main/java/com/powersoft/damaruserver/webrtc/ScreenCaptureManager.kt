package com.powersoft.damaruserver.webrtc
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.util.Log
import org.webrtc.*
import java.util.Timer
import java.util.TimerTask

class ScreenCaptureManager(
    private val context: Context,
    private val intent: Intent,
    private val videoSource: VideoSource
) {

    private companion object {
        private const val TAG = "ScreenCaptureManager"
        private const val FRAME_AVAILABILITY_TIMEOUT_MS = 500L // 500 milliseconds
    }

    private var screenCapturer: ScreenCapturerAndroid? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null
    private var frameAvailabilityTimer: Timer? = null
    private var isFrameAvailable = true
    private var captureAttempts = 0
    private val maxCaptureAttempts = 3

    fun startScreenCapturing() {
        if (captureAttempts >= maxCaptureAttempts) {
            Log.e(TAG, "Screen capturing failed repeatedly. Stopping attempts.")
            return
        }

        surfaceTextureHelper = SurfaceTextureHelper.create(
            Thread.currentThread().name, EglBase.create(null, EglBase.CONFIG_PLAIN).eglBaseContext
        )

        screenCapturer = ScreenCapturerAndroid(intent, object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                captureAttempts++
            }
        })

        try {
            screenCapturer?.initialize(
                surfaceTextureHelper!!, context, videoSource.capturerObserver
            )
            screenCapturer?.startCapture(
                context.resources.displayMetrics.widthPixels,
                context.resources.displayMetrics.heightPixels, 60
            )
            startFrameAvailabilityCheck()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting screen capture: ${e.message}", e)
            captureAttempts++
        }
    }

    private fun startFrameAvailabilityCheck() {
        frameAvailabilityTimer?.cancel()
        frameAvailabilityTimer = Timer()
        frameAvailabilityTimer!!.schedule(object : TimerTask() {
            override fun run() {
                if (!isFrameAvailable) {
                    Log.e(TAG, "Frame availability timed out. Restarting screen capture.")
                }
                isFrameAvailable = false
            }
        }, FRAME_AVAILABILITY_TIMEOUT_MS)
    }

    private fun restartScreenCapturing() {
        frameAvailabilityTimer?.cancel()

        screenCapturer?.stopCapture()
        screenCapturer?.dispose()
        screenCapturer = null

        surfaceTextureHelper?.dispose()
        surfaceTextureHelper = null

        surfaceTextureHelper = SurfaceTextureHelper.create(
            Thread.currentThread().name, EglBase.create(null, EglBase.CONFIG_PLAIN).eglBaseContext
        )

        screenCapturer = ScreenCapturerAndroid(intent, object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                Log.e(TAG, "onStop: Screen capture Stopped")
                captureAttempts++
            }
        })

        try {
            screenCapturer?.initialize(
                surfaceTextureHelper!!, context, videoSource.capturerObserver
            )
            screenCapturer?.startCapture(
                context.resources.displayMetrics.widthPixels,
                context.resources.displayMetrics.heightPixels, 30
            )
            startFrameAvailabilityCheck()
        } catch (e: Exception) {
            Log.e(TAG, "Error restarting screen capture: ${e.message}", e)
        }
    }
}
