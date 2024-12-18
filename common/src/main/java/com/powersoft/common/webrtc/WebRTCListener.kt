package com.powersoft.common.webrtc

import com.powersoft.common.model.DataModel


interface WebRTCListener {
    abstract fun onDataChannelConnected()
    fun onTransferEventToSocket(data: DataModel)
    fun onChannelMessage(message: String)
}