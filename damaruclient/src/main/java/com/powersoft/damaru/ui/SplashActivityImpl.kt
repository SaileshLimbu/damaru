package com.powersoft.damaru.ui

import android.annotation.SuppressLint
import android.content.Intent
import com.powersoft.common.ui.SplashActivity

@SuppressLint("CustomSplashScreen")
class SplashActivityImpl : SplashActivity() {
    override fun onNavigate(navigateTo: String) {
        when (navigateTo) {
            "dashboard" -> {
                startActivity(Intent(this@SplashActivityImpl, MainActivity::class.java))
                finish()
            }

            "resetPin" -> {
                startActivity(
                    Intent(this@SplashActivityImpl, PinActivityImpl::class.java)
                        .putExtra("resetPin", true)
                )
                finish()
            }

            else -> {
                startActivity(Intent(this@SplashActivityImpl, LoginActivityImpl::class.java))
                finish()
            }
        }
    }
}