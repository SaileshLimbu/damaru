package com.powersoft.damaruserver.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.bluetooth.BluetoothClass.Device
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.powersoft.common.model.GestureAction
import com.powersoft.common.model.GestureCommand

class DeviceControlService: AccessibilityService() {

    companion object{
        private var instance: DeviceControlService? = null
        fun getInstance(): DeviceControlService? = instance
    }

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

    private fun performSwipe(startX: Float, startY: Float, endX: Float, endY: Float) {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 500))  // 500ms duration for swipe
            .build()

        dispatchGesture(gesture, null, null)
    }

    fun performGesture(command: GestureCommand) {
        Log.d("damaru", "performGesture: ${command.action}")
        when(command.action){
            GestureAction.TAP -> performTap(command.startX!!, command.startY!!)
            GestureAction.LONG_PRESS -> TODO()
            GestureAction.SWIPE -> performSwipe(command.startX!!, command.startY!!, command.endX!!, command.endY!!)
            GestureAction.PINCH_ZOOM -> TODO()
            GestureAction.BACK -> performGlobalAction(GLOBAL_ACTION_BACK)
            GestureAction.HOME -> performGlobalAction(GLOBAL_ACTION_HOME)
            GestureAction.RECENT -> performGlobalAction(GLOBAL_ACTION_RECENTS)
        }
    }
}