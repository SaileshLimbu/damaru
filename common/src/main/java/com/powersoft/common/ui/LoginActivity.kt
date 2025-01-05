package com.powersoft.common.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import com.powersoft.common.R
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.databinding.ActivityLoginBinding
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class LoginActivity : BaseActivity() {
    lateinit var b: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    abstract fun onLoginResponse(any: Any, errorMessage: String?)

    override fun getViewModel(): BaseViewModel {
        return loginViewModel
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
            if (!validate()) return@setOnClickListener
            loginViewModel.login(b.etUsername.text.toString(),
                b.etPassword.text.toString(),
                b.etPin.text.toString(),
                object : ResponseCallback {
                    override fun onResponse(any: Any, errorMessage: String?) {
                        onLoginResponse(any, errorMessage)
                    }
                })
        }
    }

    private fun validate(): Boolean {
        val email = b.etUsername.text.toString()
        val password = b.etPassword.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show()
            return false
        } else if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}