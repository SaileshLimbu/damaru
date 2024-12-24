package com.powersoft.damaru.repository

import com.google.gson.Gson
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.getUnknownError
import com.powersoft.common.webservice.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepo @Inject constructor(private val apiService: ApiService, private val gson: Gson) {

    suspend fun accountsListTask(): ResponseWrapper<Any> {
        return try {
            val response = apiService.getAccountsApi()
            if (response.isSuccessful) {
                response.body()?.let {
                    ResponseWrapper.success(it)
                } ?: ResponseWrapper.error(getUnknownError("Something went wrong (Code 632)"))
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
            ResponseWrapper.error(getUnknownError("Something went wrong (Code 34523)"))
        }
    }
}