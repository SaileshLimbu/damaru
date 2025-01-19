package com.powersoft.damaruadmin.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.powersoft.common.model.LoginEntity
import com.powersoft.common.ui.LoginActivity
import com.powersoft.common.utils.AlertUtils
import com.powersoft.common.utils.hide

class LoginActivityImpl : LoginActivity() {
    override fun onLoginResponse(any: Any, errorMessage: String?) {
        if (errorMessage == null) {
            if (any is LoginEntity && any.firstLogin) {
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
            AlertUtils.showMessage(this@LoginActivityImpl, getString(com.powersoft.common.R.string.error), errorMessage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b.viewPin.hide()
        if (Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) == "9815d69e0c489f25") {
            //SuperAdmin, AndroidUser
            b.etUsername.setText("superadmin@damaru.com")
            b.etPassword.setText("hello@123")
        }
    }
}