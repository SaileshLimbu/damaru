package com.powersoft.damaruadmin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.DeviceRepo
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.damaruadmin.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepo: UserRepo,
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
            when (val response = userRepo.deleteUserTask(id)) {
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

    fun linkDevices(deviceIds: List<String>, userId: String, responseCallback: ResponseCallback) {
        showLoader()
        viewModelScope.launch {
            val response = repo.linkDevicesToUser(deviceIds, userId)
            withContext(Dispatchers.Main) {
                hideLoader()
                when (response) {
                    is ResponseWrapper.Success -> {
                        responseCallback.onResponse(response.data ?: Any(), null)
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

    fun unlinkDevice(userId: String, deviceIds: List<String>, responseCallback: ResponseCallback) {
        showLoader()
        viewModelScope.launch {
            val response = repo.unlinkDeviceFromUser(deviceIds, userId)
            withContext(Dispatchers.Main) {
                hideLoader()
                when (response) {
                    is ResponseWrapper.Success -> {
                        responseCallback.onResponse(response.data ?: Any(), null)
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

    fun extendDeviceExpiry(userId: String, deviceIds: String, days : String, responseCallback: ResponseCallback) {
        showLoader()
        viewModelScope.launch {
            val response = repo.extendDeviceExpiry(deviceIds, userId, days)
            withContext(Dispatchers.Main) {
                hideLoader()
                when (response) {
                    is ResponseWrapper.Success -> {
                        responseCallback.onResponse(response.data ?: Any(), null)
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
}