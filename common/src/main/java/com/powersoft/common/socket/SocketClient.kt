package com.powersoft.common.socket

import android.util.Log
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketClient @Inject constructor(private val gson: Gson) {

    private lateinit var user: String

    companion object {
        const val TAG = "DAMARU_SERVER-SocketClient"
        private var webSocket: WebSocketClient? = null
    }

    private lateinit var listener: SocketListener

    fun init(user: String, socketListener: SocketListener) {
        this.user = user
        this.listener = socketListener
        webSocket = object : WebSocketClient(URI("ws://10.0.0.112:3000")){
            override fun onOpen(handshakedata: ServerHandshake?) {
                sendMessageToSocket(DataModel(DataModelType.SignIn, user, null, null))
            }

            override fun onMessage(message: String?) {
                val model = gson.fromJson(message.toString(), DataModel::class.java)
                listener.onNewMessageReceived(model)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(5000)
                    init(user, socketListener)
                }
            }

            override fun onError(ex: Exception?) {
                Log.d(TAG, ex?.message ?: "no msg")
            }

        }
        webSocket?.connect()
    }

    fun sendMessageToSocket(message: Any?) {
        Log.d(TAG, "sendMessageToSocket ($user) : $message")
        webSocket?.send(gson.toJson(message))
    }

    fun onDestroy() {
        webSocket?.close()
    }
}