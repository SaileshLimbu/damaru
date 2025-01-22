package com.powersoft.common.socket

import android.util.Log
import com.google.gson.Gson
import com.powersoft.common.model.DataModel
import com.powersoft.common.model.DataModelType
import com.powersoft.common.utils.HOST_URL
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketClient @Inject constructor(private val gson: Gson) {

    companion object {
        const val TAG = "DAMARU"
        const val OFFER = "Offer"
        const val ANSWER = "Answer"
        const val ICE_CANDIDATE = "IceCandidate"
        const val DISCONNECT = "Disconnect"
    }

    private lateinit var user: String
    private lateinit var socket: Socket
    private lateinit var listener: SocketListener
    private var forceCloseSocketByClient = false

    fun init(user: String, socketListener: SocketListener, token: String, isEmulator: Boolean) {
        this.user = user
        this.listener = socketListener
        Log.e(TAG, "Connecting to socket")

        val options = IO.Options.builder()
            .setExtraHeaders(mapOf("Authorization" to listOf("Bearer $token")))
            .build()

        socket = IO.socket("$HOST_URL/signaling", options)

        socket.on(Socket.EVENT_CONNECT) {
            Log.e(TAG, "Websocket Connected")
            if (isEmulator) sendMessageToSocket(DataModelType.StartStreaming, DataModel(target = user))
            listener.onSocketConnected()
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            //if Socket is closed by client then don't connect it again
            it.forEach {
                Log.e(TAG, "Socket Closed: $it" )
            }
            if (forceCloseSocketByClient) return@on
            if (!socket.isActive) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(5000)
                    init(user, socketListener, token, isEmulator)
                }
            }
        }

        socket.on(DISCONNECT) { message ->
            val data = message[0] as JSONObject
            val model = gson.fromJson(data.toString(), DataModel::class.java)
            listener.onNewMessageReceived(DataModelType.Disconnect, model)
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) { error ->
            Log.e(TAG, "Socket Connection Error ${gson.toJson(error)}")
        }

        socket.on(OFFER) { message ->
            if (isEmulator) {
                val offer = message[0] as JSONObject
                val model = gson.fromJson(offer.toString(), DataModel::class.java)
                listener.onNewMessageReceived(DataModelType.Offer, model)
            }
        }

        socket.on(ANSWER) { message ->
            if (!isEmulator) {
                val answer = message[0] as JSONObject
                val model = gson.fromJson(answer.toString(), DataModel::class.java)
                listener.onNewMessageReceived(DataModelType.Answer, model)
            }
        }

        socket.on(ICE_CANDIDATE) { message ->
            val dataModel = message[0] as JSONObject
            val model = gson.fromJson(dataModel.toString(), DataModel::class.java)
            listener.onNewMessageReceived(DataModelType.IceCandidate, model)
        }

        socket.connect()
    }

    fun sendMessageToSocket(type: DataModelType, message: Any?) {
        if (socket.isActive) {
            val data = JSONObject(gson.toJson(message))
            socket.emit(type.toString(), data)
        } else {
            Log.e(TAG, "WebSocket is not connected. Unable to send message.")
        }
    }

    fun closeSocket() {
        forceCloseSocketByClient = true
        socket.close()
    }
}