package com.powersoft.damaruserver.repositories

import com.google.gson.Gson
import com.powersoft.damaruserver.service.ServerApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ServerRepository @Inject constructor(
    private val apiService: ServerApiService,
    private val gson: Gson
) {
    suspend fun registerEmulator(deviceId: String, deviceName: String, token: String) {
        val requestBody = mapOf(
            "device_id" to deviceId,
            "device_name" to deviceName
        )
        try {
            apiService.registerEmulator(gson.toJson(requestBody).toRequestBody(), token)
        }catch (_: Exception){
            //ignored
        }
    }

    suspend fun uploadScreenShot(deviceId: String, file: MultipartBody.Part, token: String){
        try {
            apiService.uploadScreenShot(deviceId, file, token)
        }catch (_: Exception){
            //ignored
        }
    }
}