package com.powersoft.common.repository

import com.google.gson.Gson
import com.powersoft.common.model.LoginEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.webservice.ApiService
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepo @Inject constructor(private val apiService: ApiService, private val gson: Gson) {

    suspend fun loginTask(email: String, password: String, pin: String): ResponseWrapper<LoginEntity> {
        return try {
            val params = mapOf(
                "email" to email,
                "password" to password,
                "pin" to pin
            )
            val response = apiService.loginApi(gson.toJson(params).toRequestBody())
            if (response.status) {
                if (response.data != null) {
                    ResponseWrapper.success(response.data)
                } else {
                    ResponseWrapper.error(response.message)
                }
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 8437)")
        }
    }

//    suspend fun resetPinTask(accountId : String, pin: String): ResponseWrapper<LoginEntity> {
//        return try {
//            val params = hashMapOf(
//                "pin" to pin
//            )
//            val response = apiService.updateAccountApi(accountId, gson.toJson(params).toRequestBody())
//            if (response.isSuccessful) {
//                response.body()?.let {
//                    ResponseWrapper.success(it)
//                } ?: ResponseWrapper.error(getUnknownError("Something went wrong (Code 342)"))
//            } else {
//                val errorResponse = try {
//                    val errorBody = response.errorBody()?.string()
//                    val error = gson.fromJson(errorBody, ErrorResponse::class.java)
//                    error
//                } catch (e: Exception) {
//                    getUnknownError()
//                }
//
//                ResponseWrapper.error(errorResponse)
//            }
//        } catch (e: Exception) {
//            ResponseWrapper.error(getUnknownError("Something went wrong (Code 726)"))
//        }
//    }

}
