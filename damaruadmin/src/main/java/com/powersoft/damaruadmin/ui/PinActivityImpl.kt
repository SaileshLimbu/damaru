package com.powersoft.damaruadmin.ui

import android.content.Intent
import com.powersoft.common.R
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.ui.PinActivity
import com.powersoft.common.ui.helper.AlertHelper

class PinActivityImpl : PinActivity() {
    override fun onPinVerified() {

    }

    override fun onPinResetResponse(any: Any, errorResponse: ErrorResponse?) {
        if (errorResponse == null) {
            if (resetPin) {
                startActivity(Intent(this@PinActivityImpl, AdminMainActivity::class.java))
            }
            return
        } else {
            AlertHelper.showAlertDialog(
                this@PinActivityImpl, errorResponse.message?.error ?: getString(R.string.error),
                errorResponse.message?.message ?: getString(R.string.error)
            )
        }
    }

    override fun onLogout(any: Any, errorResponse: ErrorResponse?) {
        startActivity(
            Intent(this@PinActivityImpl, LoginActivityImpl::class.java).setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        )
    }
}