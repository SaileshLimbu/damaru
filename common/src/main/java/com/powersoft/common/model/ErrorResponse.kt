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

fun getUnknownError(msg : String = "UnknownError", title : String = "Error") : ErrorResponse{
    return ErrorResponse(
        Message(
            error = title,
            message = msg,
            statusCode = 500
        ), 500
    )
}