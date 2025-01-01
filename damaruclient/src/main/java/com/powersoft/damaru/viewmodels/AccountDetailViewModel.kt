package com.powersoft.damaru.viewmodels

import androidx.lifecycle.viewModelScope
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.AccountsRepo
import com.powersoft.common.ui.helper.ResponseCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val accountRepo: AccountsRepo
): BaseViewModel() {

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