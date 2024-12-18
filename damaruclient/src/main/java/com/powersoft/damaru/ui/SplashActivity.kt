package com.powersoft.damaru.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.powersoft.damaru.R
import com.powersoft.damaru.databinding.ActivitySplashBinding
import com.powersoft.damaru.viewmodels.SplashViewModel
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

        splashViewModel.navigateToMain.observe(this) { navigateTo ->
            when (navigateTo) {
                "userPin" -> {
                    startActivity(Intent(this@SplashActivity, PinActivity::class.java))
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
