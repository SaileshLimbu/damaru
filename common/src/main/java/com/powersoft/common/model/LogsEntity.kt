package com.powersoft.common.model


import com.google.gson.annotations.SerializedName

data class LogsEntity(
    @SerializedName("action")
    val action: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("timestamp")
    val timestamp: String
)