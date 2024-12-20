package com.powersoft.damaru.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.powersoft.damaru.BuildConfig
import com.powersoft.damaru.base.BaseActivity
import com.powersoft.damaru.base.BaseViewModel
import com.powersoft.damaru.databinding.ActivityLoginBinding
import com.powersoft.damaru.models.ErrorResponse
import com.powersoft.damaru.ui.helper.AlertHelper
import com.powersoft.damaru.ui.helper.ResponseCallback
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
            loginViewModel.login(b.etUsername.text.toString(), b.etPassword.text.toString(), object : ResponseCallback {
                override fun onResponse(any: Any, errorResponse: ErrorResponse?) {
                    if (errorResponse == null) {
                        startActivity(Intent(applicationContext, MainActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    } else {
                        AlertHelper.showAlertDialog(
                            this@LoginActivity, errorResponse.message?.error ?: "Error",
                            errorResponse.message?.message ?: "Error"
                        )
                    }
                }
            })
        }

        if (BuildConfig.DEBUG) {
            //superadmin@damaru.com
            //superAdmin@123
            //SuperAdmin, AndroidUser
            b.etUsername.setText("theone@gmail.com")
            b.etPassword.setText("Test@123")
            //pin : 05007
        }
    }
}