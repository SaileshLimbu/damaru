package com.d1vivek.projectz.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.d1vivek.projectz.databinding.ActivityDeviceControlBinding
import com.d1vivek.projectz.socket.SocketClient
import com.d1vivek.projectz.utils.DataModel
import com.d1vivek.projectz.webrtc.WebrtcClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeviceControlActivity @Inject constructor(
    private val socketClient: SocketClient,
    private val webrtcClient: WebrtcClient
) : AppCompatActivity(), SocketClient.Listener {
    private lateinit var binding: ActivityDeviceControlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityDeviceControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        socketClient.listener = this
        socketClient.init()
    }

    override fun onNewMessageReceived(model: DataModel) {
    }
}