package com.powersoft.common.di

import com.powersoft.common.BuildConfig
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.webservice.ApiService
import com.powersoft.common.webservice.HeaderInterceptor
import com.powersoft.common.webservice.RequestInterceptor
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class NetworkModule {

    @Provides
    @Singleton
    open fun provideRetrofit(userRepo: UserRepo): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor(userRepo))
            .addInterceptor(RequestInterceptor())
//            .addInterceptor(RetryInterceptor(2))
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
//            .baseUrl("http://13.201.152.191:3000/")
            .baseUrl("https://a992-2407-1400-aa07-9e20-a49f-e7c6-385f-e192.ngrok-free.app/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Reusable
    open fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
