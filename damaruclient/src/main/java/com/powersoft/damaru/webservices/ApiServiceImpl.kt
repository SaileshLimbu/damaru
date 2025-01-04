package com.powersoft.damaru.webservices

import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.webservice.ApiService
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServiceImpl : ApiService {

    @GET("emulators")
    suspend fun getAllEmulators(): Response<List<DeviceEntity>>

    @GET("emulators")
    suspend fun getHisEmulator(@Query("accountId") accountId : String): Response<List<DeviceEntity>>

}
