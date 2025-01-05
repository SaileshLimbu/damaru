package com.powersoft.common.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginEntity(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("accountId")
    val accountId: String,
    @SerialName("accountName")
    val accountName: String,
    @SerialName("firstLogin")
    val firstLogin: Boolean,
    @SerialName("isRootUser")
    val isRootUser: Boolean,
    @SerialName("isSuperAdmin")
    val isSuperAdmin: Boolean,
    @SerialName("userId")
    val userId: String
)