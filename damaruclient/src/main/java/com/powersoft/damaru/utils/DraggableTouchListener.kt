package com.powersoft.damaru.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class DraggableTouchListener(
    private val clickThreshold: Int = 10, // Threshold for distinguishing click and drag
    private val onClick: () -> Unit
) : View.OnTouchListener {

    private var dX = 0f
    private var dY = 0f
    private var startX = 0f
    private var startY = 0f
    private var isDragging = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = v.x - event.rawX
                dY = v.y - event.rawY
                startX = event.rawX
                startY = event.rawY
                isDragging = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (abs(event.rawX - startX) > clickThreshold || abs(event.rawY - startY) > clickThreshold) {
                    isDragging = true
                }

                val parentWidth = (v.parent as View).width
                val parentHeight = (v.parent as View).height

                val newX = event.rawX + dX
                val newY = event.rawY + dY

                // Update position within bounds
                v.x = newX.coerceIn(0f, (parentWidth - v.width).toFloat())
                v.y = newY.coerceIn(0f, (parentHeight - v.height).toFloat())
            }

            MotionEvent.ACTION_UP -> {
                if (!isDragging) {
                    onClick.invoke()
                }
            }
        }
        return true
    }
}