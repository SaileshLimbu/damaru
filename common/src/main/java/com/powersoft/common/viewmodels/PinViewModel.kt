package com.powersoft.common.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.AccountsRepo
import com.powersoft.common.repository.AuthRepo
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.Logg
import com.powersoft.common.utils.PrefsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repo : AccountsRepo,
    private val prefsHelper: PrefsHelper,
    private val gson: Gson,
    private val userRepo : UserRepo
) : BaseViewModel() {

    fun resetPin(accountId : Int?, pin: String, responseCallback: ResponseCallback) {
        showLoader()
        viewModelScope.launch {
            val map = mapOf(
                "pin" to pin
            )
            when (val response = repo.updateAccountTask(accountId!!, map)) {
                is ResponseWrapper.Success -> {
                    prefsHelper.putString(PrefsHelper.USER, gson.toJson(response.data))
                    responseCallback.onResponse(response.data)
                }

                is ResponseWrapper.Error -> {
                    responseCallback.onResponse(Any(), response.message)
                }

                is ResponseWrapper.Loading -> {
                    Logg.d("Loading data...")
                }
            }
            hideLoader()
        }
    }

    fun logout(responseCallback: ResponseCallback){
        prefsHelper.clear()
        userRepo.refreshToken()
        responseCallback.onResponse(Any(), null)
    }

}