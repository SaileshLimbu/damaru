package com.d1vivek.projectz.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.d1vivek.projectz.databinding.ActivityDeviceControlBinding
import com.d1vivek.projectz.socket.SocketClient
import com.d1vivek.projectz.utils.DataModel
import com.d1vivek.projectz.utils.DataModelType
import com.d1vivek.projectz.webrtc.MyPeerObserver
import com.d1vivek.projectz.webrtc.WebrtcClient
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver
import org.webrtc.SessionDescription
import javax.inject.Inject

@AndroidEntryPoint
class DeviceControlActivity : AppCompatActivity(), SocketClient.Listener, WebrtcClient.Listener {

    @Inject
    lateinit var socketClient: SocketClient
    @Inject
    lateinit var webrtcClient: WebrtcClient
    @Inject
    lateinit var gson: Gson

    private lateinit var binding: ActivityDeviceControlBinding
    private var username: String? = null
    private var targetUsername: String? = null

    companion object {
        const val USER_NAME = "username"
        const val TARGET_USER_NAME = "targetUser"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityDeviceControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        binding.btnCheck.setOnClickListener {
            webrtcClient.call(targetUsername!!)
        }
    }

    private fun init() {
        username = intent.getStringExtra(USER_NAME)
        targetUsername = intent.getStringExtra(TARGET_USER_NAME)
        if (username.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "No Username", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        if (targetUsername.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "No target Username", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        socketClient.listener = this
        socketClient.init(username!!)

        webrtcClient.listener = this
        webrtcClient.initializeWebrtcClient(username!!, binding.surfaceView,
            object : MyPeerObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    super.onIceCandidate(p0)
                    p0?.let { webrtcClient.sendIceCandidate(it, targetUsername!!) }
                    Log.d("damaru", "onIceCandidate: ${p0.toString()}")
                }

                override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                    super.onConnectionChange(newState)
                    Log.d("damaru", "onConnectionChange: $newState")
//                    if (newState == PeerConnection.PeerConnectionState.CONNECTED){
//                        webrtcClient.createDataChannel()
//                    }
                }

                override fun onAddStream(p0: MediaStream?) {
                    super.onAddStream(p0)
                    Log.d("damaru", "onAddStream: $p0")
                    p0?.videoTracks?.get(0)?.addSink(binding.surfaceView)
                    Log.e("damaru", "onAddStream ${p0?.videoTracks?.get(0)}")
                }

                override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
                    super.onAddTrack(p0, p1)
                    Log.d("damaru", "onAddTrack: $p1")
                    p0?.SetObserver {
                        Log.e("damaru", "rtpReceiver $it")
                    }
                    p1?.get(0)?.videoTracks?.get(0)?.addSink(binding.surfaceView)
                    Log.e("damaru", "onAddStream ${p1?.get(0)?.videoTracks?.get(0)}")
                }

                @SuppressLint("ClickableViewAccessibility")
                override fun onDataChannel(p0: DataChannel?) {
                    super.onDataChannel(p0)
                    Log.d("damaru", "onDataChannel: $p0")

                    p0?.registerObserver(object : DataChannel.Observer {
                        override fun onBufferedAmountChange(p0: Long) {
                        }

                        override fun onStateChange() {
                        }

                        override fun onMessage(p0: DataChannel.Buffer?) {
                            Log.d("damaru", "onDataChannel onMessage: ${p0.toString()}")
                        }
                    })

                    binding.surfaceView.setOnTouchListener { view, event ->
                        Log.d("damaru", "onTouch")
                        val json = JSONObject()
                        json.put("x", event.x)
                        json.put("y", event.y)
                        json.put("action", event.action)
                        json.put("metaState", event.metaState)
                        webrtcClient.sendDataMessage(p0, json.toString())
//                        when (event.action) {
//                            MotionEvent.ACTION_DOWN -> {
//                                webrtcClient.sendDataMessage(p0, "down")
//                            }
//
//                            MotionEvent.ACTION_UP -> {
//                                webrtcClient.sendDataMessage(p0, "up")
//                            }
//                        }
                        true
                    }
                }
            })

        //ask emulator to start stream (send offer to me)
        socketClient.sendMessageToSocket(
            DataModel(
                type = DataModelType.StartStreaming,
                username = username!!,
                target = targetUsername,
                null
            )
        )
    }

    override fun onNewMessageReceived(model: DataModel) {
        when (model.type) {
            DataModelType.StartStreaming -> {
            }

            DataModelType.EndCall -> {
                finish()
            }

            DataModelType.Offer -> {
                webrtcClient.onRemoteSessionReceived(
                    SessionDescription(
                        SessionDescription.Type.OFFER, model.data
                            .toString()
                    )
                )
                targetUsername = model.username
                webrtcClient.answer(model.username)
            }

            DataModelType.Answer -> {
                webrtcClient.onRemoteSessionReceived(
                    SessionDescription(SessionDescription.Type.ANSWER, model.data.toString())
                )
            }

            DataModelType.IceCandidates -> {
                val candidate = try {
                    gson.fromJson(model.data.toString(), IceCandidate::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
                candidate?.let {
                    webrtcClient.addIceCandidate(it)
                }
            }

            else -> Unit
        }
    }

    override fun onTransferEventToSocket(data: DataModel) {
        socketClient.sendMessageToSocket(data)
    }

    override fun onChannelMessage(message: String) {
        // 2 way comm
    }
}