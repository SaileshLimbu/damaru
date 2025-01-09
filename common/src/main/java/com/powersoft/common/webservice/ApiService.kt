package com.powersoft.common.webservice

import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.LoginEntity
import com.powersoft.common.model.LogsEntity
import com.powersoft.common.model.ResponseData
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun loginApi(@Body payload: RequestBody): ResponseData<LoginEntity>

    @GET("accounts")
    suspend fun getAccountsApi(): ResponseData<List<AccountEntity>>

    @PUT("accounts/{accountId}")
    suspend fun updateAccountApi(@Path("accountId") accountId: String, @Body payload: RequestBody): ResponseData<LoginEntity>

    @DELETE("accounts/{id}")
    suspend fun deleteAccount(@Path("id") id: String): ResponseData<Any>

    @POST("accounts")
    suspend fun addAccount(@Body requestBody: RequestBody): ResponseData<Any>

    @GET("emulators/linkedAccounts")
    suspend fun getAccountsLinkedToDevice(@Query("deviceId") deviceId: String): ResponseData<List<AccountEntity>>

    @POST("emulators/unassign-multi-accounts")
    suspend fun unlinkAccountFromDevice(@Body requestBody: RequestBody): ResponseData<Any>

    @POST("emulators/assign-multi-emulators")
    suspend fun linkDevicesToAccount(@Body requestBody: RequestBody): ResponseData<Any>

    @POST("emulators/assign-multi-accounts")
    suspend fun linkAccountsToDevice(@Body requestBody: RequestBody): ResponseData<Any>

    @POST("emulators/link-emulators")
    suspend fun linkDevicesToUser(@Body requestBody: RequestBody): ResponseData<Any>

    @POST("emulators/unlink-emulators")
    suspend fun unlinkDeviceFromUser(@Body requestBody: RequestBody): ResponseData<Any>

    @POST("/emulators/extend-expiry")
    suspend fun extendExpiryOfDevice(@Body requestBody: RequestBody): ResponseData<Any>

    @GET("emulators")
    suspend fun getAllEmulators(): ResponseData<List<DeviceEntity>>

    @GET("accounts/{id}")
    suspend fun getHisEmulator(@Path("id") accountId : String): ResponseData<List<DeviceEntity>>

    @GET("emulators/connection-log")
    suspend fun getActivityLogs(@Query("deviceId") deviceId: String, @Query("accountId") accountId: String): ResponseData<List<String>>//ResponseData<List<LogsEntity>>
}