package com.powersoft.damaru.ui

import android.os.Bundle
import com.google.gson.Gson
import com.powersoft.common.R
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.ui.PinActivity
import com.powersoft.common.ui.helper.AlertHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangePinActivity @Inject constructor() : PinActivity() {
    private lateinit var account: AccountEntity

    @Inject
    lateinit var gson: Gson

    override fun onPinVerified() {

    }

    override fun onPinResetResponse(any: Any, errorResponse: ErrorResponse?) {
        if (errorResponse == null) {
            setResult(RESULT_OK)
            finish()
            return
        } else {
            AlertHelper.showAlertDialog(
                this@ChangePinActivity, errorResponse.message?.error ?: getString(R.string.error),
                errorResponse.message?.message ?: getString(R.string.error)
            )
        }
    }

    override fun onLogout(any: Any, errorResponse: ErrorResponse?) {
    }

    override fun getAccountId(): Int? {
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