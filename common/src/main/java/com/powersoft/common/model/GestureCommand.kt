package com.powersoft.common.model

enum class GestureAction {
    TAP,
    LONG_PRESS,
    SWIPE,
    PINCH_ZOOM,
    BACK,
    HOME,
    RECENT
}

data class GestureCommand(
    val action: GestureAction,
    val startX: Float? = null,
    val startY: Float? = null,
    val endX: Float? = null,
    val endY: Float? = null
)