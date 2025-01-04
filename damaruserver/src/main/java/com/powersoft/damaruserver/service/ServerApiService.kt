package com.powersoft.damaruserver.service

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.POST

interface ServerApiService {

    @POST("emulators")
    suspend fun registerEmulator(
        @Body requestBody: RequestBody,
        @Header("Authorization") token: String
    )
}