package com.powersoft.damaruserver.dispatcher

import android.content.Context
import android.view.MotionEvent
import java.lang.ref.WeakReference

class Dispatcher(context: Context) : DispatcherBase() {
    init {
        dispatcherBase = Dispatcher26(WeakReference(context))
    }

    override fun dispatch(motionEvent: MotionEvent?): Boolean {
        return dispatcherBase.dispatch(motionEvent)
    }

    override fun getWeakReference(): WeakReference<Context> {
        return dispatcherBase.getWeakReference()
    }

}