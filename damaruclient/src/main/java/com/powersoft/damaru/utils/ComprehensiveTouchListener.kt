package com.powersoft.damaru.utils

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.view.GestureDetectorCompat
import org.webrtc.SurfaceViewRenderer
import kotlin.math.abs

class ComprehensiveTouchListener(
    private val context: Context,
    private val surfaceViewRenderer: SurfaceViewRenderer
) : View.OnTouchListener {

    // Gesture Detectors
    private val gestureDetector: GestureDetectorCompat
    private val scaleGestureDetector: ScaleGestureDetector

    // Tracking variables
    private var isScaling = false
    private var initialSpan = 0f
    private var currentScaleFactor = 1f

    // Callback interfaces for different gesture events
    interface GestureCallback {
        fun onSingleTap(event: MotionEvent)
        fun onDoubleTap(event: MotionEvent)
        fun onLongPress(event: MotionEvent)
        fun onSwipe(direction: SwipeDirection)
        fun onPinchZoom(scaleFactor: Float)
        fun onPanStart(event: MotionEvent)
        fun onPan(event: MotionEvent)
        fun onPanEnd(event: MotionEvent)
    }

    // Swipe direction enum
    enum class SwipeDirection {
        UP, DOWN, LEFT, RIGHT
    }

    // Optional callback (can be null)
    var gestureCallback: GestureCallback? = null

    init {
        // Setup Gesture Detector
        gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                gestureCallback?.onSingleTap(e)
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                gestureCallback?.onDoubleTap(e)
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                gestureCallback?.onLongPress(e)
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                e1?.let {
                    val deltaX = e2.x - it.x
                    val deltaY = e2.y - it.y

                    val direction = when {
                        Math.abs(deltaX) > Math.abs(deltaY) -> {
                            if (deltaX > 0) SwipeDirection.RIGHT else SwipeDirection.LEFT
                        }
                        else -> {
                            if (deltaY > 0) SwipeDirection.DOWN else SwipeDirection.UP
                        }
                    }
                    gestureCallback?.onSwipe(direction)
                }
                return true
            }
        })

        // Setup Scale Gesture Detector
        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                currentScaleFactor *= scaleFactor

                // Optionally limit scale
                currentScaleFactor = currentScaleFactor.coerceIn(0.5f, 3f)

                gestureCallback?.onPinchZoom(currentScaleFactor)
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                isScaling = true
                initialSpan = detector.currentSpan
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                isScaling = false
            }
        })
    }

    // Tracking for pan gesture
    private var lastX = 0f
    private var lastY = 0f
    private var isPanning = false

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        // Allow both gesture detector and scale detector to process events
        val gestureHandled = gestureDetector.onTouchEvent(event)
        val scaleHandled = scaleGestureDetector.onTouchEvent(event)

        // Custom pan handling
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
                isPanning = false
                gestureCallback?.onPanStart(event)
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isScaling) {
                    val deltaX = event.x - lastX
                    val deltaY = event.y - lastY

                    // Threshold to determine if it's a pan
                    if (abs(deltaX) > 10 || abs(deltaY) > 10) {
                        isPanning = true
                        gestureCallback?.onPan(event)
                    }

                    lastX = event.x
                    lastY = event.y
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isPanning) {
                    gestureCallback?.onPanEnd(event)
                }
                isPanning = false
            }
        }

        // Return true if any gesture was handled
        return gestureHandled || scaleHandled || isPanning
    }
}

fun SurfaceViewRenderer.setComprehensiveTouchListener(
    context: Context,
    callback: ComprehensiveTouchListener.GestureCallback
) {
    val touchListener = ComprehensiveTouchListener(context, this)
    touchListener.gestureCallback = callback
    this.setOnTouchListener(touchListener)
}