package com.powersoft.damaruserver.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ServerApiService {

    @POST("emulators")
    suspend fun registerEmulator(
        @Body requestBody: RequestBody,
        @Header("Authorization") token: String
    )

    @POST("emulators/{deviceId}/screenshot")
    suspend fun uploadScreenShot(
        @Path("deviceId") deviceId: String,
        @Part file: MultipartBody.Part,
        @Header("Authorization") token: String
    )
}