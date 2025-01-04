package com.powersoft.damaruserver.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.powersoft.damaruserver.R
import com.powersoft.damaruserver.service.ScreenCaptureForegroundService
import com.powersoft.damaruserver.utils.DeviceUtils
import com.powersoft.damaruserver.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@SuppressLint("HardwareIds")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val mediaProjectionManager: MediaProjectionManager by lazy {
        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }
    private lateinit var screenCaptureLauncher: ActivityResultLauncher<Intent>

    private val deviceId by lazy {
        DeviceUtils.getDeviceId(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        screenCaptureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                startScreenCaptureService(result.data!!)
            }
        }
        startScreenCapture()
        registerEmulator()
    }

    private fun registerEmulator() {
        val deviceName = DeviceUtils.getDeviceName()
        val token = "Bearer " + getString(R.string.token)
        viewModel.registerEmulator(deviceId, deviceName, token)
    }



    private fun startScreenCapture() {
        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
        screenCaptureLauncher.launch(captureIntent)
    }

    private fun startScreenCaptureService(data: Intent) {
        val captureIntent = Intent(this, ScreenCaptureForegroundService::class.java).apply {
            action = ScreenCaptureForegroundService.ACTION_START_CAPTURE
            putExtra(ScreenCaptureForegroundService.EXTRA_RESULT_DATA, data)
            putExtra(ScreenCaptureForegroundService.EXTRA_DEVICE_ID, deviceId)
        }
        ContextCompat.startForegroundService(this, captureIntent)
    }
}