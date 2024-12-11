package com.d1vivek.projectz.socket;

import android.util.Log
import com.d1vivek.projectz.utils.DataModel
import com.d1vivek.projectz.utils.DataModelType
import com.google.gson.Gson
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
class SocketClient @Inject constructor(
    private val gson: Gson
) {
    private var username: String? = null

    companion object {
        private var webSocket: WebSocketClient? = null
    }

    var listener: Listener? = null
    fun init(username: String) {
        this.username = username

        webSocket= object : WebSocketClient(URI("ws://192.168.68.101:3000")){
//        webSocket = object : WebSocketClient(URI("ws://13.201.152.191:3000")) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                sendMessageToSocket(
                    DataModel(
                        type = DataModelType.SignIn,
                        username = username,
                        null,
                        null
                    )
                )
            }

            override fun onMessage(message: String?) {
                Log.e("damaru", "onMessage ($username): ${message ?: "fuck no msg"}")
                val model = try {
                    gson.fromJson(message.toString(), DataModel::class.java)
                } catch (e: Exception) {
                    null
                }
                model?.let {
                    listener?.onNewMessageReceived(it)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(5000)
                    init(username)
                }
            }

            override fun onError(ex: Exception?) {
                Log.e("damaru FUCK", ex?.message ?: "no msg")
            }

        }
        webSocket?.connect()
    }


    fun sendMessageToSocket(message: Any?) {
        Log.e("damaru", "sendMessageToSocket ($username) : $message")
        try {
            webSocket?.send(gson.toJson(message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onDestroy() {
        webSocket?.close()
    }

    interface Listener {
        fun onNewMessageReceived(model: DataModel)
    }
}