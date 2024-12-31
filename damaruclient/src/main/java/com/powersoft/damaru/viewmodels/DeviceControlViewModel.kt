package com.powersoft.damaru.viewmodels

import com.powersoft.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeviceControlViewModel @Inject constructor() : BaseViewModel() {
    var clientId : String = ""
    var deviceId : String = ""
}