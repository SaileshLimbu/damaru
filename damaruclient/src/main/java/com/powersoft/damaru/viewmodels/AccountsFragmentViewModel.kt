package com.powersoft.damaru.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.AccountsRepo
import com.powersoft.common.ui.helper.ResponseCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountsFragmentViewModel @Inject constructor(
    private val accountRepo: AccountsRepo
) : BaseViewModel() {
    private val _allAccounts = MutableLiveData<ResponseWrapper<List<AccountEntity>>>()

    val allAccounts: LiveData<ResponseWrapper<List<AccountEntity>>>
        get() = _allAccounts

    init {
        getAllAccounts()
    }

    fun getAllAccounts() {
        viewModelScope.launch {
            _allAccounts.postValue(accountRepo.getAccountsTask())
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
}