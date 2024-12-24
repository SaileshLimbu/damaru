package com.powersoft.damaruadmin.di

import com.powersoft.damaruadmin.webservices.ApiServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class AppModule {

    @Provides
    @Singleton
    open fun provideApiService(retrofit: Retrofit): ApiServiceImpl {
        return retrofit.create(ApiServiceImpl::class.java)
    }

}