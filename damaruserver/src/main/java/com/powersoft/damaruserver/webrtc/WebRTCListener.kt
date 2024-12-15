package com.powersoft.damaruserver.webrtc

import com.powersoft.common.model.DataModel


interface WebRTCListener {
    fun onTransferEventToSocket(data: DataModel)
    fun onChannelMessage(message: String)
}