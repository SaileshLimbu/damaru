package com.powersoft.damaru.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.getUnknownError
import com.powersoft.damaru.models.Device
import com.powersoft.damaru.webservices.ApiServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val apiService: ApiServiceImpl,
    private val gson: Gson
) : BaseViewModel() {
    private val _allDevices = MutableLiveData<ResponseWrapper<List<Device>>>()

    val allDevices: LiveData<ResponseWrapper<List<Device>>>
        get() = _allDevices

    init {
        getMyEmulators()
    }

    fun getMyEmulators() {
        viewModelScope.launch {
            try {
                _allDevices.postValue(ResponseWrapper.loading())
                val response = apiService.getMyEmulators()
                if (response.isSuccessful) {
                    _allDevices.postValue(response.body()?.let {
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
                    _allDevices.postValue(ResponseWrapper.error(errorResponse))
                }
            } catch (e: Exception) {
                _allDevices.postValue(ResponseWrapper.error(getUnknownError("Something went wrong (Code 37265)")))
            }
        }
    }
}