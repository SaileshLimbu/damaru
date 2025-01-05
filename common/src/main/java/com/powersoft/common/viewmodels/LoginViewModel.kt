package com.powersoft.common.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.AuthRepo
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.Logg
import com.powersoft.common.utils.PrefsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repo: AuthRepo,
    private val prefsHelper: PrefsHelper,
    private val gson: Gson,
    private val userRepo: UserRepo
) : BaseViewModel() {

    fun login(email: String, password: String, pin: String, responseCallback: ResponseCallback) {
        showLoader()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repo.loginTask(email, password, pin)
            withContext(Dispatchers.Main){
                when (response) {
                    is ResponseWrapper.Success -> {
                        prefsHelper.putString(PrefsHelper.USER, gson.toJson(response.data))
                        userRepo.refreshToken()
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
    }
}