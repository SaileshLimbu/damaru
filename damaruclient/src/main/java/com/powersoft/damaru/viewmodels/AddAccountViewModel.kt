package com.powersoft.damaru.viewmodels

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.getUnknownError
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.Logg
import com.powersoft.damaru.webservices.ApiServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val apiServiceImpl: ApiServiceImpl,
    private val gson: Gson
) : BaseViewModel() {

    fun addAccount(accountName: String, responseCallback: ResponseCallback) {
        if (accountName.isEmpty()) {
            responseCallback.onResponse(Any(), getUnknownError("Account name cannot be empty"))
            return
        }
        showLoader()
        viewModelScope.launch {
            val responseWrapper = try {
                val response = apiServiceImpl.addAccount(
                    "{\"account_name\":\"$accountName\"}".toRequestBody()
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        ResponseWrapper.success(it)
                    } ?: ResponseWrapper.error(getUnknownError("Something went wrong (Code 2143)"))
                } else {
                    val errorResponse = try {
                        val errorBody = response.errorBody()?.string()
                        val error = gson.fromJson(errorBody, ErrorResponse::class.java)
                        error
                    } catch (e: Exception) {
                        getUnknownError()
                    }

                    ResponseWrapper.error(errorResponse)
                }
            } catch (e: Exception) {
                ResponseWrapper.error(getUnknownError("Something went wrong (Code 8437)"))
            }

            hideLoader()
            when (responseWrapper) {
                is ResponseWrapper.Success -> {
                    responseCallback.onResponse(responseWrapper.data)
                }

                is ResponseWrapper.Error -> {
                    responseCallback.onResponse(Any(), responseWrapper.errorResponse)
                }

                is ResponseWrapper.Loading -> {
                    Logg.d("Loading data...")
                }
            }
        }
    }

}