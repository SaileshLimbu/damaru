package com.powersoft.damaru.utils

import android.view.MotionEvent
import android.view.View
import com.powersoft.common.model.GestureAction
import com.powersoft.common.model.GestureCommand
import kotlin.math.abs

class GestureDetector(
    val onGestureDetected: (GestureCommand) -> Unit
) {
    private var startX = 0f
    private var startY = 0f
    private var lastX = 0f
    private var lastY = 0f
    private var isMoving = false
    private var startTime = 0L

    // Add support for multi-touch gestures
    private var pointerCount = 0
    private var initialDistance = 0f

    fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                lastX = event.x
                lastY = event.y
                startTime = System.currentTimeMillis()
                pointerCount = event.pointerCount
                isMoving = false
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - startX
                val deltaY = event.y - startY

                // Multi-touch detection (pinch/zoom)
                if (event.pointerCount > 1) {
                    handleMultiTouchGesture(event)
                    return true
                }

                // Swipe detection
                if (abs(deltaX) > 50 || abs(deltaY) > 50) {
                    isMoving = true
                    val command = GestureCommand(
                        GestureAction.SWIPE,
                        startX,
                        startY,
                        event.x,
                        event.y
                    )
                    onGestureDetected(command)
                }
            }

            MotionEvent.ACTION_UP -> {
                val endTime = System.currentTimeMillis()
                val duration = endTime - startTime

                // Tap detection
                if (!isMoving && abs(event.x - startX) < 50 && abs(event.y - startY) < 50) {
                    // Distinguish between tap and long press
                    val command = if (duration > 500) {
                        // Long press
                        GestureCommand(GestureAction.LONG_PRESS, event.x, event.y)
                    } else {
                        // Tap
                        GestureCommand(GestureAction.TAP, event.x, event.y)
                    }
                    onGestureDetected(command)
                }

                // Reset flags
                isMoving = false
                pointerCount = 0
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                // Multi-touch started
                if (event.pointerCount == 2) {
                    initialDistance = calculateDistance(event)
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                pointerCount = event.pointerCount
            }
        }
        return true
    }

    private fun handleMultiTouchGesture(event: MotionEvent) {
        if (event.pointerCount == 2) {
            val currentDistance = calculateDistance(event)
            val scaleFactor = currentDistance / initialDistance

            // Pinch/Zoom detection
            val command = GestureCommand(
                GestureAction.PINCH_ZOOM,
                event.getX(0),
                event.getY(0),
                scaleFactor
            )
            onGestureDetected(command)

            // Update initial distance
            initialDistance = currentDistance
        }
    }

    private fun calculateDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt(x * x + y * y)
    }
}