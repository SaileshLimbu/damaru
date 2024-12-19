package com.powersoft.damaru.webservice

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun loginApi(@Body payload : RequestBody): Response<ResponseBody>
}
