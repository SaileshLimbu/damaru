package com.powersoft.damaruadmin.viewmodels

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.damaruadmin.webservices.ApiServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class AddEmulatorViewModel @Inject constructor(
    private val apiService: ApiServiceImpl,
    private val gson : Gson
) : BaseViewModel() {

    fun addEmulator(name: String, id: String, responseCallback: ResponseCallback) {
        showLoader()
        viewModelScope.launch {
            try {
                val map = mapOf(
                    "device_name" to name,
                    "device_id" to id
                )
                val response = apiService.addEmulator(gson.toJson(map).toRequestBody())
                if (response.status) {
                    responseCallback.onResponse(response.data?:Any())
                } else {
                    responseCallback.onResponse(Any(), response.message)
                }
            } catch (e: Exception) {
                responseCallback.onResponse(Any(), "Something went wrong (Code 3768)")
            }

            hideLoader()
        }
    }

//    fun editUser(id: String, name: String, email: String, responseCallback: ResponseCallback) {
//        if (name.isEmpty()) {
//            AlertHelper.showToast(context, context.getString(R.string.please_enter_name))
//        } else if (email.isEmpty()) {
//            AlertHelper.showToast(context, context.getString(R.string.please_enter_email))
//        } else {
//            showLoader()
//            viewModelScope.launch {
//                try {
//                    val map = mapOf(
//                        "name" to name,
//                        "email" to email
//                    )
//                    val response = apiService.editUser(id, gson.toJson(map).toRequestBody())
//                    if (response.status) {
//                        responseCallback.onResponse(response.data!!)
//                    } else {
//                        responseCallback.onResponse(Any(), response.message)
//                    }
//                } catch (e: Exception) {
//                    responseCallback.onResponse(Any(), "Something went wrong (Code 8437)")
//                }
//
//                hideLoader()
//            }
//        }
//    }
}