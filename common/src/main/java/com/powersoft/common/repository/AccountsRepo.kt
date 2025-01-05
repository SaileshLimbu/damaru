package com.powersoft.common.repository

import com.google.gson.Gson
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.webservice.ApiService
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AccountsRepo @Inject constructor(private val apiService: ApiService, private val gson: Gson) {

    suspend fun getAccountsTask(): ResponseWrapper<List<AccountEntity>> {
        return try {
            val response = apiService.getAccountsApi()
            if (response.status) {
                if (response.data.isNullOrEmpty()) {
                    ResponseWrapper.error("No Emulators Found")
                } else {
                    ResponseWrapper.success(response.data)
                }
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 37265)")
        }
    }

    suspend fun deleteAccountTask(id: Int): ResponseWrapper<Any> {
        return try {
            val response = apiService.deleteAccount(id.toString())
            if (response.status) {
                ResponseWrapper.success(data = Any())
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 37265)")
        }
    }

    suspend fun addAccountTask(accountName: String): ResponseWrapper<Any> {
        return try {
            val params = mapOf(
                "account_name" to accountName
            )
            val response = apiService.addAccount(gson.toJson(params).toRequestBody())
            if (response.status) {
                ResponseWrapper.success(data = Any())
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 8437)")
        }
    }

    suspend fun updateAccountTask(accountId: Int, data: Map<String, String>): ResponseWrapper<Any> {
        return try {
            val response = apiService.updateAccountApi(accountId.toString(), gson.toJson(data).toRequestBody())
            if (response.status) {
                ResponseWrapper.success(data = Any())
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 8437)")
        }
    }

    suspend fun getLinkedAccountsToDevice(deviceId: String): ResponseWrapper<List<AccountEntity>> {
        return try {
            val response = apiService.getAccountsLinkedToDevice(deviceId)
            if (response.status) {
                if (response.data.isNullOrEmpty()) {
                    ResponseWrapper.error("No Accounts Linked")
                } else {
                    ResponseWrapper.success(response.data)
                }
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 7632)")
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
            if (response.status) {
                ResponseWrapper.success(response.data)
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 7632)")
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
            if (response.status) {
                ResponseWrapper.success(response.data)
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 7632)")
        }
    }
}