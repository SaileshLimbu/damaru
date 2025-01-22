package com.powersoft.damaruserver.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.accessibility.AccessibilityEvent
import com.powersoft.common.model.GestureAction
import com.powersoft.common.model.GestureCommand
import com.powersoft.damaruserver.dispatcher.Dispatcher
import com.powersoft.damaruserver.dispatcher.InjectInterface

class DeviceControlService : AccessibilityService() {

    companion object {
        const val TAG = "DeviceControlService"
        private var instance: DeviceControlService? = null
        fun getInstance(): DeviceControlService? = instance
    }

    @Volatile
    private var injectInterface: InjectInterface = Dispatcher(this)


    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("AccessibilityService", "Accessibility Service connected.")
    }

    private fun performTap(x: Float, y: Float) {
        val path = Path().apply {
            moveTo(x, y)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
            .build()


        dispatchGesture(gesture, null, null)
    }

    private fun performLongPress(x: Float, y: Float) {
        val path = Path().apply {
            moveTo(x, y)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 2000))
            .build()


        dispatchGesture(gesture, null, null)
    }

    private fun performSwipe(startX: Float, startY: Float, endX: Float, endY: Float) {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 50))  // 500ms duration for swipe
            .build()

        dispatchGesture(gesture, null, null)
    }

    private fun update(x: Float, y: Float, action: Int) {
            var motionEvent: MotionEvent

            Handler(Looper.getMainLooper()).postDelayed({
                if (x.toInt() >= 0 && y.toInt() >= 0) {
                    motionEvent = MotionEvent.obtain(
                        SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        action, x, y, 0
                    )
                    injectInterface.dispatch(
                        motionEvent
                    )
                    motionEvent.recycle()

                }
            }, 1)
    }

    fun performGesture(command: GestureCommand) {
        Log.d(TAG, "performGesture: $command")
        when (command.action) {
            GestureAction.TAP -> performTap(command.startX!!, command.startY!!)
            GestureAction.LONG_PRESS -> performLongPress(command.startX!!, command.startY!!)
            GestureAction.SWIPE -> performSwipe(command.startX!!, command.startY!!, command.endX!!, command.endY!!)
            GestureAction.PINCH_ZOOM -> update(command.startX!!, command.startY!!, command.event!!)
            GestureAction.BACK -> performGlobalAction(GLOBAL_ACTION_BACK)
            GestureAction.HOME -> performGlobalAction(GLOBAL_ACTION_HOME)
            GestureAction.RECENT -> performGlobalAction(GLOBAL_ACTION_RECENTS)
            GestureAction.FLASH -> Unit
        }
    }
}