package com.powersoft.damaru.utils

import android.content.res.Resources.getSystem

class ExtensionUtils {
    val Int.dp: Int get() = (this / getSystem().displayMetrics.density).toInt()

    val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()

}