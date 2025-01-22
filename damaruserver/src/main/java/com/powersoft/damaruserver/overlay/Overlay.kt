package com.powersoft.damaruserver.overlay

import android.content.Context

class Overlay(context: Context) : OverlayView(context) {
    init {
        overlayRemote = OverlayViewRemote(context)
    }

    override fun dispatchKey(i: Int, i2: Int) {
        overlayRemote?.dispatchKey(i, i2)
    }
}