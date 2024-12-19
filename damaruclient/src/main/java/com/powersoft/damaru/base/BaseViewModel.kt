package com.powersoft.damaru.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun showLoader(){
        _loading.value = true
    }

    fun hideLoader(){
        _loading.value = false
    }

}