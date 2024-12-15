package com.powersoft.damaruserver.di

import android.content.Context
import com.powersoft.common.webrtc.WebRTCClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DamaruServerModule {

    @Singleton
    @Provides
    fun provideWebRTCClient(@ApplicationContext context: Context) = WebRTCClient(context)

}