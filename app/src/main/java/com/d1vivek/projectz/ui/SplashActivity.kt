package com.d1vivek.projectz.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.d1vivek.projectz.R
import com.d1vivek.projectz.databinding.ActivitySplashBinding
import com.d1vivek.projectz.viewmodels.SplashViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var b: ActivitySplashBinding
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(b.root)

//        b.splashImage.setImageDrawable(resources.getDrawableForDensity(R.mipmap.ic_launcher, DisplayMetrics.DENSITY_XXXHIGH, theme))
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation)
        b.splashImage.startAnimation(fadeInAnimation)

        splashViewModel.navigateToMain.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
//                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                val intent = Intent(this@SplashActivity, DeviceUsersActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
