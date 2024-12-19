package com.powersoft.damaru.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.powersoft.damaru.BuildConfig
import com.powersoft.damaru.base.BaseActivity
import com.powersoft.damaru.base.BaseViewModel
import com.powersoft.damaru.databinding.ActivityLoginBinding
import com.powersoft.damaru.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var b: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun getViewModel(): BaseViewModel {
        return loginViewModel
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
            loginViewModel.login(b.etUsername.text.toString(), b.etPassword.text.toString()) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
        }

        if (BuildConfig.DEBUG) {
            b.etUsername.setText("aaa@bbb.com")
            b.etPassword.setText("Test@123")
        }
    }
}