package com.powersoft.damaruadmin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.UserEntity
import com.powersoft.common.repository.DeviceRepo
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.damaruadmin.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AdminHomeFragmentViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val deviceRepo: DeviceRepo
) : BaseViewModel() {
    private val _allDevices = MutableLiveData<ResponseWrapper<List<DeviceEntity>>>()

    val allDevices: LiveData<ResponseWrapper<List<DeviceEntity>>>
        get() = _allDevices

    private val _allUsersList = MutableLiveData<ResponseWrapper<List<UserEntity>>>()

    val allUsersList: LiveData<ResponseWrapper<List<UserEntity>>>
        get() = _allUsersList

    init {
        getAllMyUsers()
    }

    fun getAllDevices() {
        viewModelScope.launch(Dispatchers.IO) {
            _allDevices.postValue(ResponseWrapper.loading())
            val response = deviceRepo.getAllDevices()
            _allDevices.postValue(response)
        }
    }

    fun getAllMyUsers() {
        _allDevices.postValue(ResponseWrapper.loading())
        viewModelScope.launch {
            val response = userRepo.getAllUsers()
            _allUsersList.postValue(response)
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
            val response = deviceRepo.linkDevicesToUser(deviceIds, userId)
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