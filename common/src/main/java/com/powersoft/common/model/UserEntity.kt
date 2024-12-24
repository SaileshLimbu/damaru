package com.powersoft.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    @SerialName("accessToken")
    val accessToken: String?,
    @SerialName("firstLogin")
    val firstLogin: Boolean?,
    @SerialName("isRootUser")
    val isRootUser: Boolean?,
    @SerialName("isSuperAdmin")
    val isSuperAdmin: Boolean?,
    @SerialName("userId")
    val userId: Int?
)