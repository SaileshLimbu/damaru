package com.powersoft.common.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    @SerialName("account_name")
    val accountName: String?,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("first_login")
    val firstLogin: Boolean?,
    @SerialName("id")
    val id: Int?,
    @SerialName("is_admin")
    val isAdmin: Boolean?,
    @SerialName("last_login")
    val lastLogin: String?,
    @SerialName("pin")
    val pin: String?,
    @SerialName("updated_at")
    val updatedAt: String?
)