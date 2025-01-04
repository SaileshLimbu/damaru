package com.powersoft.damaruserver.viewmodels

import androidx.lifecycle.viewModelScope
import com.powersoft.common.base.BaseViewModel
import com.powersoft.damaruserver.repositories.ServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val serverRepository: ServerRepository
): BaseViewModel() {

    fun registerEmulator(deviceId: String, deviceName: String, token: String){
        viewModelScope.launch(Dispatchers.IO) {
            serverRepository.registerEmulator(deviceId, deviceName, token)
        }
    }
}