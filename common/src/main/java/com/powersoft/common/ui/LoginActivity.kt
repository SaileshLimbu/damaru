package com.powersoft.common.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.databinding.ActivityLoginBinding
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class LoginActivity : BaseActivity() {
    lateinit var b: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    abstract fun onLoginResponse(any: Any, errorResponse: ErrorResponse?)

    override fun getViewModel(): BaseViewModel {
        return loginViewModel
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
            loginViewModel.login(b.etUsername.text.toString(), b.etPassword.text.toString(),
                b.etPin.text.toString(), object : ResponseCallback {
                    override fun onResponse(any: Any, errorResponse: ErrorResponse?) {
                        onLoginResponse(any, errorResponse)
                    }
                })
        }
    }
}