package com.powersoft.damaru.di

import com.powersoft.damaru.BuildConfig
import com.powersoft.damaru.repository.UserRepo
import com.powersoft.damaru.webservice.ApiService
import com.powersoft.damaru.webservice.HeaderInterceptor
import com.powersoft.damaru.webservice.RequestInterceptor
import dagger.Module
import dagger.Provides
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
            .addInterceptor(logging)
//            .addInterceptor(RetryInterceptor(2))
            .addInterceptor(RequestInterceptor())
            .addInterceptor(HeaderInterceptor(userRepo))
            .build()

        return Retrofit.Builder()
            .baseUrl("http://13.201.152.191:3000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    open fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
