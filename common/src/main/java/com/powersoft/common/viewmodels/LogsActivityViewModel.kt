package com.powersoft.common.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersoft.common.BuildConfig
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

    fun getDeviceLogs(deviceId : String, accountId : String) {
        viewModelScope.launch(Dispatchers.IO) {
            _allLogs.postValue(ResponseWrapper.loading())
            var responseWrapper = try {
                val response = apiService.getActivityLogs(deviceId, accountId)
                if (response.status) {
                    if (response.data.isNullOrEmpty()) {
                        ResponseWrapper.error("No Devices")
                    } else {
                        ResponseWrapper.success(response.data.map {
                            LogsEntity(it, "", "2024-02-02 23:12:34")
                        })
                    }
                } else {
                    ResponseWrapper.error(response.message)
                }
            } catch (e: Exception) {
                ResponseWrapper.error("Something went wrong (Code 34523)")
            }
            if(BuildConfig.DEBUG){
                responseWrapper = ResponseWrapper.success(listOf(
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34"),
                    LogsEntity("<b>theone</b> logged into device <b>Samsung S24 Ultra</b>", "", "2024-02-02 23:12:34")
                ))
            }
            _allLogs.postValue(responseWrapper)
        }
    }

}