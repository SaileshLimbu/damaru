package com.powersoft.common.utils

import com.powersoft.common.model.GestureCommand

object AspectRatioUtils {
    fun normalizeControllerCoordinates(
        screenWidth: Int,
        screenHeight: Int,
        command: GestureCommand,
        padding: Int
    ): GestureCommand {
        command.startX?.let { command.startX = it / screenWidth }
        command.startY?.let { command.startY = it / (screenHeight - padding) }
        command.endX?.let { command.endX = it / screenWidth }
        command.endY?.let { command.endY = it / (screenHeight - padding) }
        return command
    }

    fun normalizeServerCoordinate(screenWidth: Int, screenHeight: Int, command: GestureCommand): GestureCommand {
        command.startX?.let { command.startX = it * screenWidth }
        command.startY?.let { command.startY = it * screenHeight }
        command.endX?.let { command.endX = it * screenWidth }
        command.endY?.let { command.endY = it * screenHeight }
        return command
    }
}