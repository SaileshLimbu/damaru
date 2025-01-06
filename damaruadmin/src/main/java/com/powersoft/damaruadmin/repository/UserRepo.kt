package com.powersoft.damaruadmin.repository

import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.UserEntity
import com.powersoft.damaruadmin.webservices.ApiServiceImpl
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val apiService: ApiServiceImpl
) {

    suspend fun getAllUsers(): ResponseWrapper<List<UserEntity>> {
        return try {
            val response = apiService.getAllUsers()
            if (response.status) {
                if (response.data.isNullOrEmpty()) {
                    ResponseWrapper.error("No Emulators Found")
                } else {
                    ResponseWrapper.success(response.data!!)
                }
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 37265)")
        }
    }

    suspend fun deleteUserTask(id: String): ResponseWrapper<Any> {
        return try {
            val response = apiService.deleteUser(id)
            if (response.status) {
                ResponseWrapper.Success(Any())
            } else {
                ResponseWrapper.error(response.message)
            }
        } catch (e: Exception) {
            ResponseWrapper.error("Something went wrong (Code 635)")
        }
    }
}