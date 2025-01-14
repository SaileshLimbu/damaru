package com.powersoft.common.model


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    @SerializedName("accountsCount")
    val accountsCount: Int,
    @SerializedName("emulatorsCount")
    val emulatorCount: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("isSuperAdmin")
    val isSuperAdmin: Boolean,
    @SerializedName("pin")
    val pin: String
)