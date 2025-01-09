package com.powersoft.damaru.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.PickerEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.AccountsRepo
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.repository.DeviceRepo
import com.powersoft.common.utils.Logg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val accountRepo: AccountsRepo,
    private val deviceRepo: DeviceRepo,
    private val gson: Gson
) : BaseViewModel() {
    private val _allDevices = MutableLiveData<ResponseWrapper<List<DeviceEntity>>>()

    val allDevices: LiveData<ResponseWrapper<List<DeviceEntity>>>
        get() = _allDevices

    private val _allLinkedDevices = MutableLiveData<ResponseWrapper<List<DeviceEntity>>>()

    val allLinkedDevices: LiveData<ResponseWrapper<List<DeviceEntity>>>
        get() = _allLinkedDevices

    fun getAllDevices() {
        showLoader()
        _allDevices.postValue(ResponseWrapper.loading())
        viewModelScope.launch {
            val response = deviceRepo.getAllDevices()
            withContext(Dispatchers.Main) {
                hideLoader()
                _allDevices.postValue(response)
            }
        }
    }

    fun getLinkedDevices(accountId: String) {
        _allLinkedDevices.postValue(ResponseWrapper.loading())
        viewModelScope.launch {
            val response = deviceRepo.getEmulatorOfAccount(accountId)
            withContext(Dispatchers.Main) {
                _allLinkedDevices.postValue(response)
            }
        }
    }

    fun deleteAccount(id: String, responseCallback: ResponseCallback) {
        showLoader()
        viewModelScope.launch {
            val response = accountRepo.deleteAccountTask(id)
            withContext(Dispatchers.Main) {
                hideLoader()
                when (response) {
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

    fun linkDevices(deviceIds: List<String>, userId: String, accountId: String, responseCallback: ResponseCallback) {
        showLoader()
        viewModelScope.launch {
            val response = accountRepo.linkDevicesToAccount(deviceIds, userId, accountId)
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

    fun getFilteredList(): List<PickerEntity> {
        if (_allLinkedDevices.value is ResponseWrapper.Success && _allDevices.value is ResponseWrapper.Success) {
            val newList = (_allDevices.value as ResponseWrapper.Success).data.toMutableList()
            newList.removeAll { item -> ((_allLinkedDevices.value) as ResponseWrapper.Success<List<DeviceEntity>>).data.any { it.deviceId == item.deviceId } }
            return newList.map { PickerEntity(it.deviceName, gson.toJson(it)) }
        } else if (_allDevices.value is ResponseWrapper.Success) {
            return (_allDevices.value as ResponseWrapper.Success).data.map { PickerEntity(it.deviceName, gson.toJson(it)) }
        } else {
            return listOf()
        }
    }

    fun unlinkDevice(deviceId : String, userId : String, accountIds : List<String>, responseCallback: ResponseCallback) {
        showLoader()
        viewModelScope.launch {
            val responseWrapper = accountRepo.unlinkAccountsFromDevice(deviceId, userId, accountIds)
            withContext(Dispatchers.Main) {
                hideLoader()
                when (responseWrapper) {
                    is ResponseWrapper.Success -> {
                        responseCallback.onResponse(responseWrapper.data ?: Any())
                    }

                    is ResponseWrapper.Error -> {
                        responseCallback.onResponse(Any(), responseWrapper.message)
                    }

                    is ResponseWrapper.Loading -> {
                        Logg.d("Loading data...")
                    }
                }
            }
        }
    }
}