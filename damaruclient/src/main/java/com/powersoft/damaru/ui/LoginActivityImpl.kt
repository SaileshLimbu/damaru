package com.powersoft.damaru.ui

import android.content.Intent
import android.os.Bundle
import com.powersoft.common.BuildConfig
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.model.LoginEntity
import com.powersoft.common.model.UserEntity
import com.powersoft.common.ui.LoginActivity
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.damaru.R

class LoginActivityImpl : LoginActivity() {
    override fun onLoginResponse(any: Any, errorResponse: ErrorResponse?) {
        if (errorResponse == null) {
            if (any is LoginEntity && any.firstLogin == true) {
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
            AlertHelper.showAlertDialog(
                this@LoginActivityImpl, errorResponse.message?.error ?: getString(R.string.error),
                errorResponse.message?.message ?: getString(R.string.error)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            //superadmin@damaru.com
            //superAdmin@123
            //SuperAdmin, AndroidUser
            b.etUsername.setText("fuck@fuck.com")
            b.etPassword.setText("Fuck@123")
            b.etPin.setText("00000")
        }
    }
}