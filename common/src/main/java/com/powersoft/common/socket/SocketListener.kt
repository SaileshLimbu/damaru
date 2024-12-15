package com.powersoft.common.socket

import com.powersoft.common.model.DataModel

interface SocketListener {
    fun onNewMessageReceived(model: DataModel)
}