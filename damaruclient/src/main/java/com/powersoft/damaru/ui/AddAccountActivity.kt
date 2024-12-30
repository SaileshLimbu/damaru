package com.powersoft.damaru.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.damaru.R
import com.powersoft.damaru.databinding.ActivityAddAccountBinding
import com.powersoft.damaru.viewmodels.AddAccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAccountActivity : BaseActivity() {
    private val viewModel: AddAccountViewModel by viewModels()
    private lateinit var binding: ActivityAddAccountBinding

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.addAccount(binding.etName.text.toString(),
                object : ResponseCallback {
                    override fun onResponse(any: Any, errorResponse: ErrorResponse?) {
                        if (errorResponse != null) {
                            AlertHelper.showAlertDialog(
                                this@AddAccountActivity, (errorResponse.message?.error ?: getString(R.string.error)),
                                errorResponse.message?.message ?: ""
                            )
                        } else {
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                })
        }
    }
}