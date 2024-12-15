package com.d1vivek.projectz.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.accessibilityservice.GestureDescription
import android.graphics.Path

class DeviceControlService: AccessibilityService() {
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    class RemoteControlService : AccessibilityService() {

        override fun onAccessibilityEvent(event: AccessibilityEvent?) {
            // Process accessibility events if needed
        }

        override fun onInterrupt() {
            // Handle interruptions
        }

        fun performClick(x: Float, y: Float) {
            val path = Path().apply {
                moveTo(x, y)
            }

            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
                .build()

            dispatchGesture(gesture, null, null)
        }

        fun performSwipe(startX: Float, startY: Float, endX: Float, endY: Float) {
            val path = Path().apply {
                moveTo(startX, startY)
                lineTo(endX, endY)
            }

            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 500))
                .build()

            dispatchGesture(gesture, null, null)
        }
    }

}