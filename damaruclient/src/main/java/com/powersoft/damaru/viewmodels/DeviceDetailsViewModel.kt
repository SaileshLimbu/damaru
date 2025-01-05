package com.powersoft.damaru.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.AccountEntity
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
    private val accountRepo: AccountsRepo
) : BaseViewModel() {

    private val _allAccounts = MutableLiveData<ResponseWrapper<List<AccountEntity>>>()

    val allAccounts: LiveData<ResponseWrapper<List<AccountEntity>>>
        get() = _allAccounts

    var deviceId : String = ""

    fun getLinkedAccounts(deviceId : String) {
        viewModelScope.launch {
            _allAccounts.postValue(accountRepo.getLinkedAccountsToDevice(deviceId))
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
}