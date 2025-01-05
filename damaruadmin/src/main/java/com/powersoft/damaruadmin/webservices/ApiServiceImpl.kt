package com.powersoft.damaruadmin.webservices

import com.powersoft.common.model.ResponseData
import com.powersoft.common.model.UserEntity
import com.powersoft.common.webservice.ApiService
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServiceImpl : ApiService {
    @GET("users")
    suspend fun getAllUsers(): ResponseData<List<UserEntity>>

    @POST("users")
    suspend fun addUser(@Body requestBody: RequestBody): ResponseData<Any>

}
