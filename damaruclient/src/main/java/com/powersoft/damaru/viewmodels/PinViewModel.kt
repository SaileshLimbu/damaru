package com.powersoft.damaru.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.utils.PrefsHelper
import com.powersoft.damaru.base.BaseViewModel
import com.powersoft.damaru.models.ResponseWrapper
import com.powersoft.damaru.repository.AuthRepo
import com.powersoft.damaru.repository.UserRepo
import com.powersoft.damaru.ui.helper.ResponseCallback
import com.powersoft.damaru.utils.Logg
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repo : AuthRepo,
    private val prefsHelper: PrefsHelper,
    private val gson: Gson,
    private val userRepo : UserRepo
) : BaseViewModel() {

    fun resetPin(pin: String, responseCallback: ResponseCallback) {
        showLoader()
        viewModelScope.launch {
            when (val response = repo.resetPinTask("1"/*userRepo.userEntity.id*/, pin)) {
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