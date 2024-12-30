package com.powersoft.common.di

import android.content.Context
import com.google.gson.Gson
import com.powersoft.common.utils.PrefsHelper
import com.powersoft.common.webrtc.WebRTCManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class AppModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context) = context

    @Singleton
    @Provides
    fun provideGson(): Gson = Gson()

    @Singleton
    @Provides
    fun providePrefs(@ApplicationContext context: Context): PrefsHelper {
        return PrefsHelper(context)
    }
}