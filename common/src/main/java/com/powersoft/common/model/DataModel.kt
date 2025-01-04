package com.powersoft.common.model

import com.google.gson.annotations.SerializedName

enum class DataModelType{
    StartStreaming, Disconnect, Offer, Answer, IceCandidate
}

data class DataModel(
    @SerializedName("clientId")
    val username: String? = null,
    @SerializedName("deviceId")
    val target: String? = null,
    val sdp: String? = null,
    val iceCandidate: String? = null,
    val isEmulator: Boolean = false
)
