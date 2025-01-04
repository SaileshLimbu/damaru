package com.powersoft.common.model


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountEntity(
    @SerializedName("account_name")
    val accountName: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("first_login")
    val firstLogin: Boolean?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_admin")
    val isAdmin: Boolean = false,
    @SerializedName("last_login")
    val lastLogin: String?,
    @SerializedName("pin")
    val pin: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)