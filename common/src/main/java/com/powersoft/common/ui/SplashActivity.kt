package com.powersoft.common.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.powersoft.common.R
import com.powersoft.common.databinding.ActivitySplashBinding
import com.powersoft.common.viewmodels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
abstract class SplashActivity : AppCompatActivity() {

    private lateinit var b: ActivitySplashBinding
    private val splashViewModel: SplashViewModel by viewModels()

    abstract fun onNavigate(navigateTo : String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(b.root)

        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation)
        b.splashImage.startAnimation(fadeInAnimation)

        splashViewModel.navigateToMain.observe(this) { navigateTo ->
            onNavigate(navigateTo)
        }
    }
}
