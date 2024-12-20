package com.powersoft.damaru.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    @SerialName("accessToken")
    val accessToken: String?,
    @SerialName("firstLogin")
    val firstLogin: Boolean?
)