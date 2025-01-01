package com.powersoft.damaru.ui

import android.content.Intent
import com.powersoft.common.R
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.ui.PinActivity
import com.powersoft.common.ui.helper.AlertHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PinActivityImpl : PinActivity() {
    @Inject
    lateinit var userRepo: UserRepo

    override fun onPinVerified() {

    }

    override fun onPinResetResponse(any: Any, errorResponse: ErrorResponse?) {
        if (errorResponse == null) {
            startActivity(Intent(this@PinActivityImpl, MainActivity::class.java))
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

    override fun getAccountId(): Int? {
        return userRepo.seasonEntity.value?.accountId
    }

    override fun isChangePin(): Boolean {
        return false
    }
}