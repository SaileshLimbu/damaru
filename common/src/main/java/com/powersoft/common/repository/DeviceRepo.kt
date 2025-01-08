package com.powersoft.common.repository

import com.google.gson.Gson
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.webservice.ApiService
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepo @Inject constructor(private val apiService: ApiService, private val gson: Gson) {

    suspend fun getAllDevices(): ResponseWrapper<List<DeviceEntity>> {
        return try {
            val response = apiService.getAllEmulators()
            if (response.status) {
                if (response.data.isNullOrEmpty()) {
                    ResponseWrapper.error("No Devices")
                } else {
                    ResponseWrapper.success(response.data)
                }
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 34523)")
        }
    }

    suspend fun getEmulatorOfAccount(accountId: String): ResponseWrapper<List<DeviceEntity>> {
        return try {
            val response = apiService.getHisEmulator(accountId)
            if (response.status) {
                if (response.data.isNullOrEmpty()) {
                    ResponseWrapper.error("No Devices")
                } else {
                    ResponseWrapper.success(response.data)
                }
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 34523)")
        }
    }

    suspend fun linkDevicesToUser(deviceIds: List<String>, userId: String): ResponseWrapper<Any?> {
        return try {
            val params = mapOf(
                "deviceIds" to deviceIds,
                "userId" to userId,
            )
            val response = apiService.linkDevicesToUser(gson.toJson(params).toRequestBody())
            if (response.status) {
                ResponseWrapper.success(response.data)
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 2354)")
        }
    }

    suspend fun unlinkDeviceFromUser(deviceIds: List<String>, userId: String): ResponseWrapper<Any?> {
        return try {
            val params = mapOf(
                "deviceIds" to deviceIds,
                "userId" to userId,
            )
            val response = apiService.unlinkDeviceFromUser(gson.toJson(params).toRequestBody())
            if (response.status) {
                ResponseWrapper.success(response.data)
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 54345)")
        }
    }

    suspend fun extendDeviceExpiry(deviceId: String, userId: String, days : String): ResponseWrapper<Any?> {
        return try {
            val params = mapOf(
                "deviceId" to deviceId,
                "userId" to userId,
                "days" to days
            )
            val response = apiService.extendExpiryOfDevice(gson.toJson(params).toRequestBody())
            if (response.status) {
                ResponseWrapper.success(response.data)
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 54345)")
        }
    }
}