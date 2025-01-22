package com.powersoft.damaruserver.overlay

import android.content.Context
import android.widget.FrameLayout


abstract class OverlayView(context: Context) : FrameLayout(context) {
    var overlayRemote: OverlayView? = null

    /**
     * Dispatch key.
     *
     * @param i  the
     * @param i2 the 2
     */
    abstract fun dispatchKey(i: Int, i2: Int)
}