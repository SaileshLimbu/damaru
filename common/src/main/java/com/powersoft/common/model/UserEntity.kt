package com.powersoft.common.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    @SerialName("accounts")
    val accounts: List<Account?>?,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("email")
    val email: String?,
    @SerialName("id")
    val id: Int?,
    @SerialName("name")
    val name: String?,
    @SerialName("password")
    val password: String?,
    @SerialName("updated_at")
    val updatedAt: String?
)