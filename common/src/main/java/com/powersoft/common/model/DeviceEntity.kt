package com.powersoft.common.model


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceEntity(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("device_name")
    val deviceName: String,
    @SerializedName("status")
    val status: Status,
    @SerializedName("state")
    val state: State,
    @SerializedName("expires_at")
    val expiresAt: String?,
    @SerializedName("screenshot")
    val screenshot: String?,
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("email")
    val email: String?
)

enum class Status{
    online, offline
}

enum class State{
    AVAILABLE, REGISTERED
}