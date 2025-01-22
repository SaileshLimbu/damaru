package com.powersoft.common.model

enum class GestureAction {
    TAP,
    LONG_PRESS,
    SWIPE,
    PINCH_ZOOM,
    BACK,
    HOME,
    RECENT,
    FLASH,
}

data class GestureCommand(
    val action: GestureAction,
    val event: Int? = 0,
    var startX: Float? = null,
    var startY: Float? = null,
    var endX: Float? = null,
    var endY: Float? = null
)