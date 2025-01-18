package com.powersoft.damaru.ui

import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import com.powersoft.common.R
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.LoginEntity
import com.powersoft.common.ui.PinActivity
import com.powersoft.common.utils.AlertUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangePinActivity @Inject constructor() : PinActivity() {
    private lateinit var account: AccountEntity

    @Inject
    lateinit var gson: Gson

    override fun onPinVerified() {

    }

    override fun onPinResetResponse(any: Any, errorMessage: String?) {
        if (errorMessage == null) {
            if (any is LoginEntity) {
                setResult(RESULT_OK, Intent().putExtra("pin", any.pin))
            } else {
                setResult(RESULT_OK)
            }
            finish()
            return
        } else {
            AlertUtils.showMessage(
                this@ChangePinActivity, getString(R.string.error), errorMessage
            )
        }
    }

    override fun onLogout(any: Any, errorMessage: String?) {
    }

    override fun getAccountId(): String {
        return account.id
    }

    override fun isChangePin(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        account = gson.fromJson(intent.getStringExtra("account"), AccountEntity::class.java)
    }
}