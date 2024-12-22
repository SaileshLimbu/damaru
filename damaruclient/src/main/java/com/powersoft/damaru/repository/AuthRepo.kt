package com.powersoft.damaru.repository

import com.google.gson.Gson
import com.powersoft.damaru.models.ErrorResponse
import com.powersoft.damaru.models.ResponseWrapper
import com.powersoft.damaru.models.getUnknownError
import com.powersoft.damaru.models.UserEntity
import com.powersoft.damaru.webservice.ApiService
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepo @Inject constructor(private val apiService: ApiService, private val gson: Gson) {

    suspend fun loginTask(email: String, password: String, pin: String): ResponseWrapper<UserEntity> {
        return try {
            val response = apiService.loginApi(
                "{\"email\":\"$email\",\"password\":\"$password\",\"pin\":\"$pin\"}".toRequestBody()
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    ResponseWrapper.success(it)
                } ?: ResponseWrapper.error(getUnknownError("Something went wrong (Code 2143)"))
            } else {
                val errorResponse = try {
                    val errorBody = response.errorBody()?.string()
                    val error = gson.fromJson(errorBody, ErrorResponse::class.java)
                    error
                } catch (e: Exception) {
                    getUnknownError()
                }

                ResponseWrapper.error(errorResponse)
            }
        } catch (e: Exception) {
            ResponseWrapper.error(getUnknownError("Something went wrong (Code 8437)"))
        }
    }

    suspend fun resetPinTask(userId : String, pin: String): ResponseWrapper<UserEntity> {
        return try {
            val params = hashMapOf(
                "pin" to pin
            )
            val response = apiService.resetPinTask(userId, gson.toJson(params).toRequestBody())
            if (response.isSuccessful) {
                response.body()?.let {
                    ResponseWrapper.success(it)
                } ?: ResponseWrapper.error(getUnknownError("Something went wrong (Code 342)"))
            } else {
                val errorResponse = try {
                    val errorBody = response.errorBody()?.string()
                    val error = gson.fromJson(errorBody, ErrorResponse::class.java)
                    error
                } catch (e: Exception) {
                    getUnknownError()
                }

                ResponseWrapper.error(errorResponse)
            }
        } catch (e: Exception) {
            ResponseWrapper.error(getUnknownError("Something went wrong (Code 726)"))
        }
    }

}
