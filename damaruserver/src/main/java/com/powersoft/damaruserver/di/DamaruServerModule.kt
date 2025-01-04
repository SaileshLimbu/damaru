package com.powersoft.damaruserver.di

import android.content.Context
import com.powersoft.common.webrtc.WebRTCClient
import com.powersoft.damaruserver.service.ServerApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DamaruServerModule {

    @Singleton
    @Provides
    fun provideWebRTCClient(@ApplicationContext context: Context) = WebRTCClient(context)

    @Singleton
    @Provides
    fun provideServerApiService(retrofit: Retrofit) : ServerApiService{
        return retrofit.create(ServerApiService::class.java)
    }
}