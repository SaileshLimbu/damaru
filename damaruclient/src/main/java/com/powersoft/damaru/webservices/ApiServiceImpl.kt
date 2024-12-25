package com.powersoft.damaru.webservices

import com.powersoft.common.webservice.ApiService
import com.powersoft.damaru.models.Device
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceImpl : ApiService {

    @GET("emulators")
    suspend fun getMyEmulators(): Response<Device>

}
