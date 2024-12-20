package com.powersoft.damaru.viewmodels

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.utils.PrefsHelper
import com.powersoft.damaru.R
import com.powersoft.damaru.base.BaseViewModel
import com.powersoft.damaru.models.ResponseWrapper
import com.powersoft.damaru.repository.AuthRepo
import com.powersoft.damaru.ui.helper.ResponseCallback
import com.powersoft.damaru.utils.Logg
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repo: AuthRepo,
    private val prefsHelper: PrefsHelper,
    private val gson: Gson
) : BaseViewModel() {

    fun login(email: String, password: String, responseCallback: ResponseCallback) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, context.getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show()
        } else {
            showLoader()
            viewModelScope.launch {
                when (val response = repo.loginTask(email, password)) {
                    is ResponseWrapper.Success -> {
                        prefsHelper.putString(PrefsHelper.USER, gson.toJson(response.data))
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