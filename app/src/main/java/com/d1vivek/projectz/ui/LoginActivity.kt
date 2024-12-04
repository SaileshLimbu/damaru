package com.d1vivek.projectz.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.d1vivek.projectz.BuildConfig
import com.d1vivek.projectz.databinding.ActivityLoginBinding
import com.d1vivek.projectz.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var b: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
            loginViewModel.login(b.etUsername.text.toString(), b.etPassword.text.toString()) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
        }

        if(BuildConfig.DEBUG){
            b.etUsername.setText("admin")
            b.etPassword.setText("pass")
            b.btnLogin.performClick()
        }
    }
}