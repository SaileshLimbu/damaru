package com.powersoft.damaru.ui

import android.content.Intent
import android.os.Bundle
import com.powersoft.common.BuildConfig
import com.powersoft.common.model.LoginEntity
import com.powersoft.common.ui.LoginActivity
import com.powersoft.common.utils.AlertUtils
import com.powersoft.damaru.R

class LoginActivityImpl : LoginActivity() {
    override fun onLoginResponse(any: Any, errorMessage: String?) {
        if (errorMessage == null) {
            if (any is LoginEntity && any.firstLogin && any.isRootUser) {
                startActivity(
                    Intent(applicationContext, PinActivityImpl::class.java)
                        .putExtra("resetPin", true)
                )
                return
            }
            startActivity(
                Intent(applicationContext, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        } else {
            AlertUtils.showMessage(this@LoginActivityImpl, getString(R.string.error), errorMessage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            //superadmin@damaru.com
            //superAdmin@123
            //SuperAdmin, AndroidUser
//            b.etUsername.setText("donotfucking@delete.com")
//            b.etPassword.setText("Test@123")
//            b.etPin.setText("00000")
        }
    }
}