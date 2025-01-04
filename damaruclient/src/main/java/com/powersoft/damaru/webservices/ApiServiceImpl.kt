package com.powersoft.damaru.webservices

import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.webservice.ApiService
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServiceImpl : ApiService {

    @GET("emulators")
    suspend fun getMyEmulators(): Response<List<DeviceEntity>>

}
