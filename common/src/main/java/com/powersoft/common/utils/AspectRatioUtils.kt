package com.powersoft.common.utils

import com.powersoft.common.model.GestureCommand

object AspectRatioUtils {
    fun normalizeControllerCoordinates(
        screenWidth: Int,
        screenHeight: Int,
        startX: Float,
        startY: Float,
        verticalPadding: Int,
        horizontalPadding: Int
    ): Pair<Float, Float> {
        val normalizedX = startX / (screenWidth - horizontalPadding)
        val normalizedY = startY / (screenHeight - verticalPadding)
        return Pair(normalizedX, normalizedY)
    }

    fun normalizeServerCoordinate(screenWidth: Int, screenHeight: Int, command: GestureCommand): GestureCommand {
        command.startX.let { command.startX = it * screenWidth }
        command.startY.let { command.startY = it * screenHeight }
        return command
    }
}