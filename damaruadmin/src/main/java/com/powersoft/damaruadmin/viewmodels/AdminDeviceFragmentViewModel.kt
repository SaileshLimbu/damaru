package com.powersoft.damaruadmin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.DeviceEntity

import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.UserEntity
import com.powersoft.common.repository.DeviceRepo
import com.powersoft.common.ui.helper.ResponseCallback

import com.powersoft.common.utils.PrefsHelper
import com.powersoft.damaruadmin.webservices.ApiServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDeviceFragmentViewModel @Inject constructor(
    private val repo: DeviceRepo
) : BaseViewModel() {
    private val _allDevices = MutableLiveData<ResponseWrapper<List<DeviceEntity>>>()

    val allDevices: LiveData<ResponseWrapper<List<DeviceEntity>>>
        get() = _allDevices

    init {
        getAllDevices()
    }

    fun getAllDevices() {
        viewModelScope.launch(Dispatchers.IO) {
            _allDevices.postValue(ResponseWrapper.loading())
            val response = repo.getAllDevices()
            _allDevices.postValue(response)
        }
    }

    fun deleteUser(id: String, responseCallback: ResponseCallback) {
            viewModelScope.launch {
                when (val response = repo.deleteEmulatorTask(id)) {
                    is ResponseWrapper.Success -> {
                        responseCallback.onResponse(response.data, null)
                    }

                    is ResponseWrapper.Error -> {
                        responseCallback.onResponse(Any(), response.message)
                    }

                    is ResponseWrapper.Loading -> {
                    }
                }
        }
    }

}