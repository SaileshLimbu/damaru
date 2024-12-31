package com.powersoft.damaru.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.getUnknownError
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.Logg
import com.powersoft.damaru.webservices.ApiServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DeviceDetailsViewModel @Inject constructor(
    private val apiService: ApiServiceImpl,
    private val gson: Gson
) : BaseViewModel() {
    private val _allAccounts = MutableLiveData<ResponseWrapper<List<AccountEntity>>>()

    val allAccounts: LiveData<ResponseWrapper<List<AccountEntity>>>
        get() = _allAccounts

    init {
        getAllAccounts()
    }

    fun getAllAccounts() {
        viewModelScope.launch {
            try {
                _allAccounts.postValue(ResponseWrapper.loading())
                val response = apiService.getAccountsApi()
                if (response.isSuccessful) {
                    _allAccounts.postValue(response.body()?.let {
                        if (response.body().isNullOrEmpty()) {
                            ResponseWrapper.error(getUnknownError("No Emulators Found"))
                        } else {
                            ResponseWrapper.success(it)
                        }
                    } ?: ResponseWrapper.error(getUnknownError("Something went wrong (Code 342)")))
                } else {
                    val errorResponse = try {
                        val errorBody = response.errorBody()?.string()
                        val error = gson.fromJson(errorBody, ErrorResponse::class.java)
                        error
                    } catch (e: Exception) {
                        getUnknownError()
                    }
                    _allAccounts.postValue(ResponseWrapper.error(errorResponse))
                }
            } catch (e: Exception) {
                _allAccounts.postValue(ResponseWrapper.error(getUnknownError("Something went wrong (Code 37265)")))
            }
        }
    }

    fun deleteAccount(id: Int, responseCallback: ResponseCallback) {
        showLoader()
        viewModelScope.launch {
            try {
                val response = apiService.deleteAccount(id.toString())
                withContext(Dispatchers.Main) {
                    hideLoader()
                }

                if (response.isSuccessful) {
                    getAllAccounts()
                } else {
                    val errorResponse = try {
                        val errorBody = response.errorBody()?.string()
                        val error = gson.fromJson(errorBody, ErrorResponse::class.java)
                        error
                    } catch (e: Exception) {
                        getUnknownError()
                    }
                    withContext(Dispatchers.Main) {
                        responseCallback.onResponse(Any(), errorResponse)
                    }
                    Logg.e("FUCK error 111   >>>>>>   ${errorResponse.message?.message}")
                }
            } catch (e: Exception) {
                Logg.e("FUCK error 2222   >>>>>>   ${e.message}")
                withContext(Dispatchers.Main) {
                    hideLoader()
                    responseCallback.onResponse(Any(), getUnknownError("Something went wrong (Code 37265)"))
                }
            }
        }
    }
}