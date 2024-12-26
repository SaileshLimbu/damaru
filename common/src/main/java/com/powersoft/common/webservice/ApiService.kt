package com.powersoft.common.webservice

import com.powersoft.common.model.LoginEntity
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun loginApi(@Body payload : RequestBody): Response<LoginEntity>

    @GET("accounts")
    suspend fun getAccountsApi(): Response<Any>

    @PUT("accounts/{accountId}")
    suspend fun resetPinTask(@Path("accountId") accountId : String, @Body payload: RequestBody): Response<LoginEntity>
}
