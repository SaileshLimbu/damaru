package com.powersoft.common.repository

import com.google.gson.Gson
import com.powersoft.common.model.UserEntity
import com.powersoft.common.utils.Logg
import com.powersoft.common.utils.PrefsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepo @Inject constructor(private val prefsHelper: PrefsHelper, private val gson: Gson) {
    private val _userEntity = MutableStateFlow<UserEntity?>(null)
    val userEntity: StateFlow<UserEntity?> = _userEntity.asStateFlow()

    init {
        _userEntity.value = try {
            gson.fromJson(prefsHelper.getString(PrefsHelper.USER), UserEntity::class.java)
        } catch (e: Exception) {
            null
        }
        Logg.e("FUCK token init >>>> ${_userEntity.value?.accessToken}")
    }

    fun refreshToken(){
        _userEntity.value = try {
            gson.fromJson(prefsHelper.getString(PrefsHelper.USER), UserEntity::class.java)
        } catch (e: Exception) {
            null
        }
        Logg.e("FUCK token refreshtoken >>>> ${_userEntity.value?.accessToken}")
    }
}