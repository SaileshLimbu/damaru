package com.powersoft.damaruadmin.webservices

import com.powersoft.common.webservice.ApiService
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceImpl : ApiService {
    @GET("users")
    suspend fun getAllUsers(): Response<Any>

}
