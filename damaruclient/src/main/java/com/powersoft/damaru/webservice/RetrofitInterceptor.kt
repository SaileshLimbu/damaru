package com.powersoft.damaru.webservice

import com.powersoft.common.utils.EncryptionHelper
import com.powersoft.common.utils.SoManager
import com.powersoft.damaru.repository.UserRepo
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

        var modifiedBody = ""
        if (isEncryptionEnabled) {
            val originalBody = originalRequest.body?.let { bodyToString(it) }

            val publicKey = SoManager().getPublicKey()
            modifiedBody = originalBody?.let { EncryptionHelper.encryptPayloadWithPublicKey(it, publicKey).toList().joinToString() } ?: "error encryption"
        }
        val zippedBody = gzip(modifiedBody.toByteArray())
        val newRequest = originalRequest.newBuilder()
            .method(originalRequest.method, zippedBody.toRequestBody(originalRequest.body?.contentType()))
            .build()

        return chain.proceed(newRequest)
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
}

class HeaderInterceptor(private val userRepo: UserRepo) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        if(userRepo.userEntity != null) {
            request.addHeader("Authorization", "Bearer ${userRepo.userEntity?.id}")
        }
        request.addHeader("Accept", if (isEncryptionEnabled) "text/plain" else "application/json")

        return chain.proceed(request.build())
    }
}

private fun gzip(data: ByteArray): ByteArray {
    val byteStream = ByteArrayOutputStream()
    val gzipStream = GZIPOutputStream(byteStream)
    gzipStream.write(data)
    gzipStream.close()
    return byteStream.toByteArray()
}




