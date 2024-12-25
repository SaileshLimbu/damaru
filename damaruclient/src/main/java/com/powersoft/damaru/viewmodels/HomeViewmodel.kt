package com.powersoft.damaru.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.damaru.models.Device
import com.powersoft.damaru.webservices.ApiServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val apiService: ApiServiceImpl
) : BaseViewModel() {
    private val _allDevices = MutableLiveData<ResponseWrapper<List<Device>>>()

    val allDevices: LiveData<ResponseWrapper<List<Device>>>
        get() = _allDevices

    init {
        getMyEmulators()
    }

    private fun getMyEmulators() {
        viewModelScope.launch {
            try {
                val response = apiService.getMyEmulators()
            } catch (e: Exception) {
                //catch
            }
        }
    }
}