package com.powersoft.damaru.ui

import android.content.Intent
import com.powersoft.common.R
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.ui.PinActivity
import com.powersoft.common.utils.AlertUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PinActivityImpl : PinActivity() {
    @Inject
    lateinit var userRepo: UserRepo

    override fun onPinVerified() {

    }

    override fun onPinResetResponse(any: Any, errorMessage: String?) {
        if (errorMessage == null) {
            startActivity(Intent(this@PinActivityImpl, MainActivity::class.java))
            return
        } else {
            AlertUtils.showMessage(
                this@PinActivityImpl, getString(R.string.error), errorMessage
            )
        }
    }

    override fun onLogout(any: Any, errorMessage: String?) {
        startActivity(
            Intent(this@PinActivityImpl, LoginActivityImpl::class.java).setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        )
    }

    override fun getAccountId(): String {
        return userRepo.seasonEntity.value?.accountId.toString()
    }

    override fun isChangePin(): Boolean {
        return false
    }
}