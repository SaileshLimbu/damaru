package com.powersoft.common.model

enum class GestureAction {
    BACK,
    HOME,
    RECENT,
    FLASH,
    EVENT
}

data class GestureCommand(
    val action: GestureAction,
    val event: Int? = 0,
    var startX: Float,
    var startY: Float
)