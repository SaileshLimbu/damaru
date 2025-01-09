package com.powersoft.damaru.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.PickerEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.AccountsRepo
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.Logg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DeviceDetailsViewModel @Inject constructor(
    private val accountRepo: AccountsRepo,
    private val gson: Gson
) : BaseViewModel() {

    private val _allAccounts = MutableLiveData<ResponseWrapper<List<AccountEntity>>>()

    val allAccounts: LiveData<ResponseWrapper<List<AccountEntity>>>
        get() = _allAccounts

    private val _allLinkedAccounts = MutableLiveData<ResponseWrapper<List<AccountEntity>>>()

    val allLinkedAccounts: LiveData<ResponseWrapper<List<AccountEntity>>>
        get() = _allLinkedAccounts

    var deviceId : String = ""

    fun getLinkedAccounts(deviceId : String) {
        viewModelScope.launch {
            _allLinkedAccounts.postValue(accountRepo.getLinkedAccountsToDevice(deviceId))
        }
    }

    fun getAllAccounts() {
        viewModelScope.launch {
            _allAccounts.postValue(accountRepo.getAccountsTask())
        }
    }

    fun getFilteredList(): List<PickerEntity> {
        if (_allLinkedAccounts.value is ResponseWrapper.Success && _allAccounts.value is ResponseWrapper.Success) {
            val newList = (_allAccounts.value as ResponseWrapper.Success).data.toMutableList()
            newList.removeAll { item -> ((_allLinkedAccounts.value) as ResponseWrapper.Success<List<AccountEntity>>).data.any { it.id == item.id } }
            return newList.map { PickerEntity(it.accountName, gson.toJson(it)) }
        } else if (_allAccounts.value is ResponseWrapper.Success) {
            return (_allAccounts.value as ResponseWrapper.Success).data.map { PickerEntity(it.accountName, gson.toJson(it)) }
        } else {
            return listOf()
        }
    }

    fun unlinkAccount(deviceId : String, userId : String, accountIds : List<String>, responseCallback: ResponseCallback) {
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

    fun linkAccounts(accountIds: List<String>, userId: String, deviceId: String, responseCallback: ResponseCallback) {
        showLoader()
        viewModelScope.launch {
            val response = accountRepo.linkAccountsToDevice(accountIds, userId, deviceId)
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