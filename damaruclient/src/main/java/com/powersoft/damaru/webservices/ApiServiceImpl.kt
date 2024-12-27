package com.powersoft.damaru.webservices

import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.webservice.ApiService
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceImpl : ApiService {

    @GET("emulators")
    suspend fun getMyEmulators(): Response<List<DeviceEntity>>

}
