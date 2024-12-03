package com.d1vivek.projectz.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.d1vivek.projectz.databinding.ActivityLoginBinding
import com.d1vivek.projectz.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {
    lateinit var b: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
//            startActivity(Intent(applicationContext, MainActivity::class.java))
            loginViewModel.startLoginFlow()
        }
    }
}