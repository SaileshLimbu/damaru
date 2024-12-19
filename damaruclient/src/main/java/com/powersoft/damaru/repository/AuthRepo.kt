package com.powersoft.damaru.repository

import com.powersoft.damaru.webservice.ApiService
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun loginTask(email: String, password: String): ResponseBody? {
        val response = apiService.loginApi(
            "{\"email\":\"$email\",\"password\":\"$password\"}".toRequestBody()
        )
        return if (response.isSuccessful) response.body() else null
    }
}
