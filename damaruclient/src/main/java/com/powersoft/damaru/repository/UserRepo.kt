package com.powersoft.damaru.repository

import com.google.gson.Gson
import com.powersoft.damaru.models.UserEntity
import com.powersoft.common.utils.PrefsHelper
import javax.inject.Inject

class UserRepo @Inject constructor(prefsHelper: PrefsHelper, gson: Gson) {
    var userEntity: UserEntity? = try {
        gson.fromJson(prefsHelper.getString(PrefsHelper.USER), UserEntity::class.java)
    } catch (e: Exception) {
        null
    }
}