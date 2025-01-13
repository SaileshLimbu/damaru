package com.powersoft.damaruadmin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.powersoft.common.base.BaseViewModel

class AdminMainActivityViewModel : BaseViewModel() {

    private val _deviceUpdate = MutableLiveData<Boolean>()
    val deviceUpdate: LiveData<Boolean> = _deviceUpdate

    fun refreshDevice() {
        _deviceUpdate.value = true
    }

    //user
    private val _userUpdate = MutableLiveData<Boolean>()
    val userUpdate: LiveData<Boolean> = _userUpdate

    fun refreshUser() {
        _userUpdate.value = true
    }

}