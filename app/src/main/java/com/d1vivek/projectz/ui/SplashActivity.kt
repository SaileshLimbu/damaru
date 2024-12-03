package com.d1vivek.projectz.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.d1vivek.projectz.R
import com.d1vivek.projectz.databinding.ActivitySplashBinding
import com.d1vivek.projectz.viewmodels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var b: ActivitySplashBinding
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(b.root)

        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation)
        b.splashImage.startAnimation(fadeInAnimation)

        splashViewModel.isEmulatorBuild = applicationContext.packageName.contains("emulator")
        splashViewModel.navigateToMain.observe(this) { navigateTo ->
            when (navigateTo) {
                "emulator" -> {
                    val deviceId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
                    startActivity(
                        Intent(this@SplashActivity, DeviceControlActivity::class.java)
                            .putExtra(DeviceControlActivity.USER_NAME, deviceId)
                    )
                    finish()
                }

                "emulatorSetup" -> {
                    startActivity(Intent(this@SplashActivity, EmulatorSetupActivity::class.java))
                    finish()
                }

                "userPin" -> {
//                    startActivity(Intent(this@SplashActivity, LoginPinActivity::class.java))
                    finish()
                }

                else -> {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}
