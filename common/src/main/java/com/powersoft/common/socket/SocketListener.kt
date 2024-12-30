package com.powersoft.common.socket

import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType

interface SocketListener {
    fun onSocketConnected()
    fun onNewMessageReceived(type: DataModelType, model: DataModel)
}