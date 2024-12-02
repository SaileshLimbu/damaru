package com.d1vivek.projectz.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.d1vivek.projectz.databinding.ActivityDeviceControlBinding
import com.d1vivek.projectz.repository.MainRepository
import com.d1vivek.projectz.service.WebrtcService
import com.d1vivek.projectz.service.WebrtcServiceRepository
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.MediaStream
import javax.inject.Inject

@AndroidEntryPoint
class DeviceControlActivity : AppCompatActivity(), MainRepository.Listener {
    private lateinit var binding: ActivityDeviceControlBinding
    private lateinit var screenCaptureLauncher: ActivityResultLauncher<Intent>

    @Inject
    lateinit var webrtcServiceRepository: WebrtcServiceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeviceControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        WebrtcService.surfaceView = binding.surfaceView
        WebrtcService.listener = this
//        webrtcServiceRepository.startIntent(username!!)
//        views.requestBtn.setOnClickListener {
//            startScreenCapture()
//        }

        screenCaptureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleScreenCaptureResult(result.resultCode, result.data)
        }

        startScreenCapture()

    }

    private fun startScreenCapture() {
        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
        screenCaptureLauncher.launch(captureIntent)
    }
    private fun handleScreenCaptureResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            WebrtcService.screenPermissionIntent = data
            webrtcServiceRepository.startIntent("test_user")
//            binding.requestBtn.setOnClickListener {
//                webrtcServiceRepository.requestConnection(
//                    binding.targetEt.text.toString()
//                )
//            }
        } else {
            Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onConnectionRequestReceived(target: String) {
//        runOnUiThread{
//            views.apply {
//                notificationTitle.text = "$target is requesting for connection"
//                notificationLayout.isVisible = true
//                notificationAcceptBtn.setOnClickListener {
//                    webrtcServiceRepository.acceptCAll(target)
//                    notificationLayout.isVisible = false
//                }
//                notificationDeclineBtn.setOnClickListener {
//                    notificationLayout.isVisible = false
//                }
//            }
//        }
    }

    override fun onConnectionConnected() {
//        runOnUiThread {
//            views.apply {
//                requestLayout.isVisible = false
//                disconnectBtn.isVisible = true
//                disconnectBtn.setOnClickListener {
//                    webrtcServiceRepository.endCallIntent()
//                    restartUi()
//                }
//            }
//        }
    }

    override fun onCallEndReceived() {
//        runOnUiThread {
//            restartUi()
//        }
    }

    override fun onRemoteStreamAdded(stream: MediaStream) {
//        runOnUiThread {
//            views.surfaceView.isVisible = true
//            stream.videoTracks[0].addSink(views.surfaceView)
//        }
    }

    private fun restartUi(){
//        views.apply {
//            disconnectBtn.isVisible=false
//            requestLayout.isVisible = true
//            notificationLayout.isVisible = false
//            surfaceView.isVisible = false
//        }
    }
}