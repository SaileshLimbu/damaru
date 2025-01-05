package com.powersoft.damaru.repository

import com.google.gson.Gson
import com.powersoft.common.model.DeviceEntity

import com.powersoft.common.model.ResponseWrapper
import com.powersoft.damaru.webservices.ApiServiceImpl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepo @Inject constructor(private val apiService: ApiServiceImpl) {

    suspend fun getAllDevices(): ResponseWrapper<List<DeviceEntity>> {
        return try {
            val response = apiService.getAllEmulators()
            if (response.status) {
                if (response.data.isNullOrEmpty()){
                    ResponseWrapper.error("No Devices")
                }else{
                    ResponseWrapper.success(response.data!!)
                }
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 34523)")
        }
    }

    suspend fun getEmulatorOfAccount(accountId : String): ResponseWrapper<List<DeviceEntity>> {
        return try {
            val response = apiService.getHisEmulator(accountId)
            if (response.status) {
                if (response.data.isNullOrEmpty()){
                    ResponseWrapper.error(response.message)
                }else{
                    ResponseWrapper.success(response.data!!)
                }
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 34523)")
        }
    }
}