package com.powersoft.damaruserver.utils

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager

/**
 *  Forces screen refresh by briefly showing a nearly invisible overlay,
 *  ensuring that ScreenCaptureAndroid captures new frames even with static content.
 *  Since the initial frame is not captured and not displaying on client app
 */
class ScreenRefresher(private val context: Context) {
    private var overlayView: View? = null
    private var windowManager: WindowManager? = null

    fun flashScreen() {
        val params = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }

        // Create semi-transparent overlay
        overlayView = View(context).apply {
            setBackgroundColor(Color.argb(1, 255, 255, 255)) // Very slight white tint
        }

        // Add and remove quickly
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        Handler(Looper.getMainLooper()).post{windowManager?.addView(overlayView, params)}

        // Remove after very short delay
        Handler(Looper.getMainLooper()).postDelayed({
            removeOverlay()
        }, 300)
    }

    private fun removeOverlay() {
        try {
            overlayView?.let { view ->
                windowManager?.removeView(view)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        overlayView = null
    }
}