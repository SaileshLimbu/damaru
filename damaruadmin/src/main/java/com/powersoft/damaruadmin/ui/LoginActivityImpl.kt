package com.powersoft.damaruadmin.ui

import android.content.Intent
import android.os.Bundle
import com.powersoft.common.BuildConfig
import com.powersoft.common.model.LoginEntity
import com.powersoft.common.ui.LoginActivity
import com.powersoft.common.ui.helper.AlertHelper

class LoginActivityImpl : LoginActivity() {
    override fun onLoginResponse(any: Any, errorMessage: String?) {
        if (errorMessage == null) {
            if (any is LoginEntity && any.firstLogin == true) {
                startActivity(
                    Intent(applicationContext, PinActivityImpl::class.java)
                        .putExtra("resetPin", true)
                )
                return
            }
            startActivity(
                Intent(applicationContext, AdminMainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        } else {
            AlertHelper.showAlertDialog(this@LoginActivityImpl, getString(com.powersoft.common.R.string.error), errorMessage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            //SuperAdmin, AndroidUser
            b.etUsername.setText("superadmin@damaru.com")
            b.etPassword.setText("hello@123")
            b.etPin.setText("05007")
        }
    }
}