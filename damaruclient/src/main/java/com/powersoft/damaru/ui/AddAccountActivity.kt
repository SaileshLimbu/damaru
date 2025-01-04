package com.powersoft.damaru.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.google.gson.Gson
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.damaru.R
import com.powersoft.damaru.databinding.ActivityAddAccountBinding
import com.powersoft.damaru.viewmodels.AddAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddAccountActivity : BaseActivity() {
    private val viewModel: AddAccountViewModel by viewModels()
    private lateinit var binding: ActivityAddAccountBinding
    private var account : AccountEntity? = null
    @Inject
    lateinit var gson: Gson

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("account")) {
            account = gson.fromJson(intent.getStringExtra("account"), AccountEntity::class.java)
            binding.etName.setText(account?.accountName)
            binding.btnSubmit.text = getString(R.string.update)
            binding.viewNote.visibility = View.GONE
            binding.noteDetail.visibility = View.GONE
            binding.title.text = getString(R.string.update_account)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            if(account != null) {
                viewModel.updateAccount(account!!.id!!, binding.etName.text.toString(),
                    object : ResponseCallback {
                        override fun onResponse(any: Any, errorResponse: ErrorResponse?) {
                            if (errorResponse != null) {
                                AlertHelper.showAlertDialog(
                                    this@AddAccountActivity, (errorResponse.message?.error ?: getString(R.string.error)),
                                    errorResponse.message?.message ?: ""
                                )
                            } else {
                                setResult(RESULT_OK, Intent().putExtra("edited_name", binding.etName.text.toString()))
                                finish()
                            }
                        }
                    })
            }else {
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
}