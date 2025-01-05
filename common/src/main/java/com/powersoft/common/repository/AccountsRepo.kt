package com.powersoft.common.repository

import com.google.gson.Gson
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.getUnknownError
import com.powersoft.common.webservice.ApiService
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AccountsRepo @Inject constructor(private val apiService: ApiService, private val gson: Gson) {

    suspend fun getAccountsTask(): ResponseWrapper<List<AccountEntity>> {
        return try {
            val response = apiService.getAccountsApi()
            if (response.isSuccessful) {
                response.body()?.let {
                    if (response.body().isNullOrEmpty()) {
                        ResponseWrapper.error(getUnknownError("No Emulators Found"))
                    } else {
                        ResponseWrapper.success(it)
                    }
                } ?: ResponseWrapper.error(getUnknownError("Something went wrong (Code 342)"))
            } else {
                ResponseWrapper.error(
                    try {
                        val errorBody = response.errorBody()?.string()
                        val error = gson.fromJson(errorBody, ErrorResponse::class.java)
                        error
                    } catch (e: Exception) {
                        getUnknownError()
                    }
                )
            }
        } catch (e: Exception) {
            ResponseWrapper.error(getUnknownError("Something went wrong (Code 37265)"))
        }
    }

    suspend fun deleteAccountTask(id: Int): ResponseWrapper<Any> {
        return try {
            val response = apiService.deleteAccount(id.toString())
            if (response.isSuccessful) {
                ResponseWrapper.success(data = Any())
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
            ResponseWrapper.error(getUnknownError("Something went wrong (Code 37265)"))
        }
    }

    suspend fun addAccountTask(accountName: String): ResponseWrapper<Any> {
        return try {
            val params = mapOf(
                "account_name" to accountName
            )
            val response = apiService.addAccount(gson.toJson(params).toRequestBody())
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

    suspend fun updateAccountTask(accountId: Int, data: Map<String, String>): ResponseWrapper<Any> {
        return try {
            val response = apiService.updateAccountApi(accountId.toString(), gson.toJson(data).toRequestBody())
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

    suspend fun getLinkedAccountsToDevice(deviceId: String): ResponseWrapper<List<AccountEntity>> {
        return try {
            val response = apiService.getAccountsLinkedToDevice(deviceId)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (response.body().isNullOrEmpty()) {
                        ResponseWrapper.error(getUnknownError("No Accounts Linked"))
                    } else {
                        ResponseWrapper.success(it)
                    }
                } ?: ResponseWrapper.error(getUnknownError("Something went wrong (Code 972)"))
            } else {
                ResponseWrapper.error(
                    try {
                        val errorBody = response.errorBody()?.string()
                        val error = gson.fromJson(errorBody, ErrorResponse::class.java)
                        error
                    } catch (e: Exception) {
                        getUnknownError(msg = e.message.toString())
                    }
                )
            }
        } catch (e: Exception) {
            ResponseWrapper.error(getUnknownError("Something went wrong (Code 7632)"))
        }
    }

    suspend fun unlinkAccountsFromDevice(deviceId: String, userId: String, accountIds: List<String>): ResponseWrapper<Any?> {
        return try {
            val params = mapOf(
                "device_id" to deviceId,
                "user_id" to userId,
                "accountIds" to accountIds
            )
            val response = apiService.unlinkAccountFromDevice(gson.toJson(params).toRequestBody())
            if (response.isSuccessful) {
                ResponseWrapper.success(response.body())
            } else {
                ResponseWrapper.error(
                    try {
                        val errorBody = response.errorBody()?.string()
                        val error = gson.fromJson(errorBody, ErrorResponse::class.java)
                        error
                    } catch (e: Exception) {
                        getUnknownError(msg = e.message.toString())
                    }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseWrapper.error(getUnknownError("Something went wrong (Code 7632)"))
        }
    }

    suspend fun linkDevicesToAccount(deviceIds: List<String>, userId: String, accountId: String): ResponseWrapper<Any?> {
        return try {
            val params = mapOf(
                "device_ids" to deviceIds,
                "user_id" to userId,
                "accountId" to accountId
            )
            val response = apiService.linkDevicesToAccount(gson.toJson(params).toRequestBody())
            if (response.isSuccessful) {
                ResponseWrapper.success(response.body())
            } else {
                ResponseWrapper.error(
                    try {
                        val errorBody = response.errorBody()?.string()
                        val error = gson.fromJson(errorBody, ErrorResponse::class.java)
                        error
                    } catch (e: Exception) {
                        getUnknownError(msg = e.message.toString())
                    }
                )
            }
        } catch (e: Exception) {
            ResponseWrapper.error(getUnknownError("Something went wrong (Code 7632)"))
        }
    }
}