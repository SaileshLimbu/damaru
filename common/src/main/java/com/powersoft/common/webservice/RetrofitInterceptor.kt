package com.powersoft.damaru.webservice

import com.powersoft.common.model.UserEntity
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.utils.EncryptionHelper
import com.powersoft.common.utils.Logg
import com.powersoft.common.utils.SoManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPOutputStream

const val isEncryptionEnabled = false

class AuthInterceptor(private val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        request = request.newBuilder()
            .addHeader("Authorization", "Bearer ${tokenProvider.getToken()}")
            .build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            synchronized(this) {
                val newToken = tokenProvider.refreshToken()
                request = request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            }
            return chain.proceed(request)
        }

        return response
    }
}

interface TokenProvider {
    fun getToken(): String
    fun refreshToken(): String
}

class RetryInterceptor(private val maxRetryCount: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var response: Response

        do {
            attempt++
            response = chain.proceed(chain.request())
        } while (!response.isSuccessful && attempt < maxRetryCount)

        return response
    }
}

class RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.method == "POST") {
            val originalBody = originalRequest.body?.let { bodyToString(it) }
            val modifiedBody = if (isEncryptionEnabled) {
                val publicKey = SoManager().getPublicKey()
                originalBody?.let { EncryptionHelper.encryptPayloadWithPublicKey(it, publicKey).toList().joinToString() } ?: "error encryption"
            } else {
                originalBody ?: ""
            }

//        val gzippedBody = gzip(modifiedBody.toByteArray())
            val newRequestBody = modifiedBody.toRequestBody(originalRequest.body?.contentType())
            val newRequest = originalRequest.newBuilder()
                .method(originalRequest.method, newRequestBody)
                .build()

            return chain.proceed(newRequest)
        }
        return chain.proceed(originalRequest)
    }

    private fun bodyToString(requestBody: RequestBody): String {
        return try {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            ""
        }
    }

    private fun gzip(data: ByteArray): ByteArray {
        val byteStream = ByteArrayOutputStream()
        val gzipStream = GZIPOutputStream(byteStream)
        gzipStream.write(data)
        gzipStream.close()
        return byteStream.toByteArray()
    }
}

class HeaderInterceptor(private val userRepo: UserRepo) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        val token : String? = userRepo.userEntity.value?.accessToken
        Logg.e("FUCK token >>>>>>>>>>>>>> $token")
        if (!token.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
        }
        request.addHeader("content-type", if (isEncryptionEnabled) "text/plain" else "application/json")
        return chain.proceed(request.build())
    }
}




