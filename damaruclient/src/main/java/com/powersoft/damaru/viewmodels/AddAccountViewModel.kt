package com.powersoft.damaru.viewmodels

import androidx.lifecycle.viewModelScope
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.getUnknownError
import com.powersoft.common.repository.AccountsRepo
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.Logg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val accountRepo: AccountsRepo,
) : BaseViewModel() {

    fun addAccount(accountName: String, responseCallback: ResponseCallback) {
        if (accountName.isEmpty()) {
            responseCallback.onResponse(Any(),"Account name cannot be empty")
            return
        }
        showLoader()
        viewModelScope.launch {
            val responseWrapper = accountRepo.addAccountTask(accountName)
            withContext(Dispatchers.Main) {
                hideLoader()
                when (responseWrapper) {
                    is ResponseWrapper.Success -> {
                        responseCallback.onResponse(responseWrapper.data)
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

    fun updateAccount(accountId : Int, accountName: String, responseCallback: ResponseCallback) {
        if (accountName.isEmpty()) {
            responseCallback.onResponse(Any(), "Account name cannot be empty")
            return
        }
        showLoader()
        viewModelScope.launch {
            val params = mapOf(
                "account_name" to accountName
            )
            val responseWrapper = accountRepo.updateAccountTask(accountId, params)
            withContext(Dispatchers.Main) {
                hideLoader()
                when (responseWrapper) {
                    is ResponseWrapper.Success -> {
                        responseCallback.onResponse(responseWrapper.data)
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