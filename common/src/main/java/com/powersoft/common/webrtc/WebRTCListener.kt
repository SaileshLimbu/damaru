package com.powersoft.common.webrtc

import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType


interface WebRTCListener {
    abstract fun onDataChannelConnected()
    fun onTransferEventToSocket(type: DataModelType, data: DataModel)
    fun onChannelMessage(message: String)
}