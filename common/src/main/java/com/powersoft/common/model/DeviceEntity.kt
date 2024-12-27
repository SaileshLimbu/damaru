package com.powersoft.common.model


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceEntity(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("device_id")
    val deviceId: String?,
    @SerializedName("device_name")
    val deviceName: String?,
    @SerializedName("expires_at")
    val expiresAt: String?,
    @SerializedName("screenshot")
    val screenshot: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)