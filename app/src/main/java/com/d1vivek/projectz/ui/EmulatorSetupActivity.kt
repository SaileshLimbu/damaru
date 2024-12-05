package com.d1vivek.projectz.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.d1vivek.projectz.BuildConfig
import com.d1vivek.projectz.databinding.ActivityEmulatorSetupBinding

class EmulatorSetupActivity : AppCompatActivity() {
    private lateinit var b: ActivityEmulatorSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityEmulatorSetupBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.backButton.setOnClickListener {
            finish()
        }

        val deviceId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
        b.etEmulatorId.setText(deviceId)

        if(BuildConfig.DEBUG){
            b.targetUserId.setText("theone")
        }

        b.btnNext.setOnClickListener {
            startActivity(
                Intent(this@EmulatorSetupActivity, DeviceShareActivity::class.java)
                    .putExtra(DeviceShareActivity.USER_NAME, deviceId)
                    .putExtra(DeviceShareActivity.TARGET_USER_NAME, b.targetUserId.text.toString())
            )
            finish()
        }
    }
}