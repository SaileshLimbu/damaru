package com.powersoft.damaruadmin.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.AlertUtils
import com.powersoft.damaruadmin.R
import com.powersoft.damaruadmin.webservices.ApiServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class AddUserViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiServiceImpl,
    private val gson: Gson
) : BaseViewModel() {

    fun addUser(name: String, email: String, password: String, confirmPassword: String, responseCallback: ResponseCallback) {
        if (name.isEmpty()) {
            AlertUtils.showMessage(context, "Error", context.getString(R.string.please_enter_name))
        } else if (email.isEmpty()) {
            AlertUtils.showMessage(context, "Error", context.getString(R.string.please_enter_email))
        } else if (password != confirmPassword) {
            AlertUtils.showMessage(context, "Error", context.getString(R.string.password_dont_match))
        } else {
            showLoader()
            viewModelScope.launch {
                try {
                    val map = mapOf(
                        "name" to name,
                        "email" to email,
                        "password" to password,
                        "role" to "AndroidUser"
                    )
                    val response = apiService.addUser(gson.toJson(map).toRequestBody())
                    if (response.status) {
                        responseCallback.onResponse(response.data ?: Any())
                    } else {
                        responseCallback.onResponse(Any(), response.message)
                    }
                } catch (e: Exception) {
                    responseCallback.onResponse(Any(), "Something went wrong (Code 8437)")
                }

                hideLoader()
            }
        }
    }

    fun editUser(id: String, name: String, email: String, responseCallback: ResponseCallback) {
        if (name.isEmpty()) {
            AlertUtils.showMessage(context, "Error", context.getString(R.string.please_enter_name))
        } else if (email.isEmpty()) {
            AlertUtils.showMessage(context, "Error", context.getString(R.string.please_enter_email))
        } else {
            showLoader()
            viewModelScope.launch {
                try {
                    val map = mapOf(
                        "name" to name,
                        "email" to email
                    )
                    val response = apiService.editUser(id, gson.toJson(map).toRequestBody())
                    if (response.status) {
                        responseCallback.onResponse(response.data!!)
                    } else {
                        responseCallback.onResponse(Any(), response.message)
                    }
                } catch (e: Exception) {
                    responseCallback.onResponse(Any(), "Something went wrong (Code 8437)")
                }

                hideLoader()
            }
        }
    }
}