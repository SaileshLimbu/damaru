package com.powersoft.damaruserver.overlay

import android.animation.TimeAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager

/**
 * Developed by Ramesh Pokhrel
 */
internal class CustomTimeAnimator(context: Context?) : View(context), TimeAnimator.TimeListener {
    private val p1 = Paint()
    private val p2 = Paint()
    private val p3 = Paint()
    private var timeAnim: TimeAnimator? = null
    private var f5267f: Long = 0
    private var f5268g: Long = 0
    private var f5269h = false
    private var f5270i = true
    private var f5271j = 15.0f
    private var f5272k = 11.5f
    private var f5273l = 0
    private var runnable: Runnable? = null
    private fun init() {
        timeAnim = TimeAnimator()
        timeAnim!!.setTimeListener(this)
        p1.color = -16776961
        p1.alpha = 200
        p1.isAntiAlias = true
        p1.style = Paint.Style.STROKE
        p2.color = -1
        p2.alpha = 200
        p2.isAntiAlias = true
        p2.style = Paint.Style.STROKE
        p3.color = -16777216
        p3.alpha = 200
        p3.isAntiAlias = true
        p3.style = Paint.Style.STROKE
        manageStrokeWidth()
    }

    private fun manageStrokeWidth() {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(displayMetrics)
        val a = displayMetrics.densityDpi
        if (a != f5273l) {
            f5273l = a
            val f = a.toFloat()
            f5271j = 5.0f * f / 160.0f
            f5272k = f * 1.5f / 160.0f
            p1.strokeWidth = f5271j
            p2.strokeWidth = f5272k
            p3.strokeWidth = f5272k
        }
    }

    /**
     * Destroy animator.
     */
    fun destroyAnimator() {
        runnable = null
        mo6749e()
        val timeAnimator = timeAnim
        timeAnim = null
        timeAnimator?.setTimeListener(null)
    }

    /**
     * Mo 6746 b.
     */
    fun mo6746b() {
        f5268g = SystemClock.uptimeMillis()
        f5269h = false
        f5270i = false
        mo6749e()
        manageStrokeWidth()
        f5267f = 0
        visibility = VISIBLE
        val timeAnimator = timeAnim
        if (timeAnimator != null) {
            timeAnimator.currentPlayTime = 0
            timeAnimator.start()
        }
    }

    /**
     * Mo 6747 c.
     */
    fun mo6747c() {
        f5268g = SystemClock.uptimeMillis()
        if (visibility != VISIBLE) {
            f5269h = true
            f5270i = true
            mo6749e()
            manageStrokeWidth()
            f5267f = 0
            visibility = VISIBLE
            val timeAnimator = timeAnim
            if (timeAnimator != null) {
                timeAnimator.currentPlayTime = 0
                timeAnimator.start()
            }
        }
    }

    /**
     * Mo 6748 d.
     */
    fun mo6748d() {
        f5268g = SystemClock.uptimeMillis()
        f5269h = false
        f5267f = 0
        mo6749e()
        f5270i = true
        val timeAnimator = timeAnim
        if (timeAnimator != null) {
            timeAnimator.currentPlayTime = 0
            timeAnimator.start()
        }
    }

    /**
     * Mo 6749 e.
     */
    fun mo6749e() {
        val timeAnimator = timeAnim
        timeAnimator?.cancel()
    }

    public override fun onDraw(canvas: Canvas) {
        val width = canvas.width.toFloat()
        val f = width / 2.0f
        val height = canvas.height.toFloat() / 2.0f
        val f2 = (width - f5271j - f5272k * 4.0f) / 2.0f
        var f3 = f5267f.toFloat() / 300.0f
        if (f5270i && f3 > 0.5f && SystemClock.uptimeMillis() - f5268g < 150) {
            val timeAnimator = timeAnim
            if (timeAnimator != null) {
                timeAnimator.currentPlayTime = 150
            }
            f3 = 0.5f
        }
        val f4 = f3 * f3
        var f5 = f3 * f4 * 2.0f - f4 * 3.0f + 1.0f
        if (f5269h && f5 > 0.5f || !f5270i) {
            f5 = 1.0f - f5
        }
        val f6 = f2 * f5
        canvas.drawCircle(f, height, f6, p1)
        val f7 = f5271j / 2.0f + f6
        val f8 = f5272k + f7
        canvas.drawCircle(f, height, f7, p2)
        canvas.drawCircle(f, height, f8, p3)
        val f9 = f6 - f5271j / 2.0f
        val f10 = f9 - f5272k
        if (f9 > 0.0f) {
            canvas.drawCircle(f, height, f9, p2)
        }
        if (f10 > 0.0f) {
            canvas.drawCircle(f, height, f10, p3)
        }
    }

    override fun onTimeUpdate(timeAnimator: TimeAnimator, j: Long, j2: Long) {
        if (j >= 300) {
            f5267f = 300
            mo6749e()
            if (f5270i) {
                visibility = INVISIBLE
                val runnable = runnable
                runnable?.run()
            }
        } else {
            f5267f = j
        }
        invalidate()
    }

    /**
     * Sets hide callback.
     *
     * @param runnable the runnable
     */
    fun setHideCallback(runnable: Runnable?) {
        this.runnable = runnable
    }

    /**
     * Instantiates a new Custom time animator.
     *
     * @param context the context
     */
    init {
        init()
    }
}