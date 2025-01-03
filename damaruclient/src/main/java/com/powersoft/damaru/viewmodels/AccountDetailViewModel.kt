package com.powersoft.damaru.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.AccountsRepo
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.damaru.repository.DeviceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val accountRepo: AccountsRepo,
    private val deviceRepo: DeviceRepo
) : BaseViewModel() {
    private val _allDevices = MutableLiveData<ResponseWrapper<List<DeviceEntity>>>()

    val allDevices: LiveData<ResponseWrapper<List<DeviceEntity>>>
        get() = _allDevices

    init {
        getHisDevices()
    }

    private fun getHisDevices() {
        _allDevices.postValue(ResponseWrapper.loading())
        viewModelScope.launch {
            val response = deviceRepo.getHisEmulators()
            withContext(Dispatchers.Main){
                _allDevices.postValue(response)
            }
        }
    }

    fun deleteAccount(id: Int, responseCallback: ResponseCallback) {
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
                        responseCallback.onResponse(Any(), response.errorResponse)
                    }

                    is ResponseWrapper.Loading -> {

                    }
                }
            }
        }
    }
}