package com.powersoft.damaruadmin.webservices

import com.powersoft.common.model.ResponseData
import com.powersoft.common.model.UserEntity
import com.powersoft.common.webservice.ApiService
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServiceImpl : ApiService {
    @GET("users")
    suspend fun getAllUsers(): ResponseData<List<UserEntity>>

    @POST("users")
    suspend fun addUser(@Body requestBody: RequestBody): ResponseData<Any>

    @PUT("users/{id}")
    suspend fun editUser(@Path("id") id : String, @Body requestBody: RequestBody): ResponseData<Any>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id : String): ResponseData<Any>

}
