package com.powersoft.common.repository

import com.google.gson.Gson
import com.powersoft.common.model.LoginEntity
import com.powersoft.common.utils.PrefsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepo @Inject constructor(private val prefsHelper: PrefsHelper, private val gson: Gson) {
    private val _seasonEntity = MutableStateFlow<LoginEntity?>(null)
    val seasonEntity: StateFlow<LoginEntity?> = _seasonEntity.asStateFlow()

    init {
        _seasonEntity.value = try {
            gson.fromJson(prefsHelper.getString(PrefsHelper.USER), LoginEntity::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun refreshToken(){
        _seasonEntity.value = try {
            gson.fromJson(prefsHelper.getString(PrefsHelper.USER), LoginEntity::class.java)
        } catch (e: Exception) {
            null
        }
    }
}