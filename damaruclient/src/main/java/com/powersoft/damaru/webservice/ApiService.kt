package com.powersoft.damaru.webservice

import com.powersoft.damaru.models.AccountsEntity
import com.powersoft.damaru.models.UserEntity
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun loginApi(@Body payload : RequestBody): Response<UserEntity>

    @GET("accounts")
    suspend fun getAccountsApi(): Response<AccountsEntity>
}
