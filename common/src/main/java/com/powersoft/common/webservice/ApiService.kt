package com.powersoft.common.webservice

import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.LoginEntity
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun loginApi(@Body payload : RequestBody): Response<LoginEntity>

    @GET("accounts")
    suspend fun getAccountsApi(): Response<List<AccountEntity>>

    @PUT("accounts/{accountId}")
    suspend fun updateAccountApi(@Path("accountId") accountId : String, @Body payload: RequestBody): Response<LoginEntity>

    @DELETE("accounts/{id}")
    suspend fun deleteAccount(@Path("id") id : String): Response<Any>

    @POST("accounts")
    suspend fun addAccount(@Body requestBody: RequestBody): Response<Any>

    @GET("emulators/linkedAccounts")
    suspend fun getAccountsLinkedToDevice(@Query("deviceId") deviceId : String): Response<List<AccountEntity>>

    @GET("emulators/unassign-multi-accounts")
    suspend fun unlinkAccountFromDevice(@Body requestBody: RequestBody): Response<Any>

    @POST("emulators/assign-multi-emulators")
    suspend fun linkDevicesToAccount(@Body requestBody: RequestBody): Response<Any>
}
