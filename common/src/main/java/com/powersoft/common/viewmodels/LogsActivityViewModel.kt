package com.powersoft.common.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.LogsEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.webservice.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsActivityViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel() {
    private val _allLogs = MutableLiveData<ResponseWrapper<List<LogsEntity>>>()

    val allLogs: LiveData<ResponseWrapper<List<LogsEntity>>>
        get() = _allLogs

    init {
        getAllDevices()
    }

    fun getAllDevices() {
        viewModelScope.launch(Dispatchers.IO) {
            _allLogs.postValue(ResponseWrapper.loading())
            val responseWrapper = try {
                val response = apiService.getActivityLogs()
                if (response.status) {
                    if (response.data.isNullOrEmpty()) {
                        ResponseWrapper.error("No Devices")
                    } else {
                        ResponseWrapper.success(response.data)
                    }
                } else {
                    ResponseWrapper.error(response.message)
                }
            } catch (e: Exception) {
                ResponseWrapper.error("Something went wrong (Code 34523)")
            }
            _allLogs.postValue(responseWrapper)
        }
    }

}