package com.powersoft.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("message")
    val message: Message?,
    @SerialName("statusCode")
    val statusCode: Int?
)

@Serializable
data class Message(
    @SerialName("error")
    val error: String?,
    @SerialName("message")
    val message: String?,
    @SerialName("statusCode")
    val statusCode: Int?
)

fun getUnknownError(msg : String = "UnknownError") : ErrorResponse{
    return ErrorResponse(
        Message(
            error = "UnknownError",
            message = msg,
            statusCode = 500
        ), 500
    )
}