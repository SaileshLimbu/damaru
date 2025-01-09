package com.powersoft.common.model


import com.google.gson.annotations.SerializedName

data class LogsEntity(
    @SerializedName("description")
    val desc: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("date")
    val date: String
)