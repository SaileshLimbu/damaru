package com.powersoft.common.viewmodels

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.R
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ResponseWrapper
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
class LoginViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repo: AuthRepo,
    private val prefsHelper: PrefsHelper,
    private val gson: Gson,
    private val userRepo: UserRepo
) : BaseViewModel() {

    fun login(email: String, password: String, pin: String, responseCallback: ResponseCallback) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, context.getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show()
        } else {
            showLoader()
            viewModelScope.launch {
                when (val response = repo.loginTask(email, password, pin)) {
                    is ResponseWrapper.Success -> {
                        prefsHelper.putString(PrefsHelper.USER, gson.toJson(response.data))
                        userRepo.refreshToken()
                        responseCallback.onResponse(response.data)
                    }

                    is ResponseWrapper.Error -> {
                        responseCallback.onResponse(Any(), response.errorResponse)
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