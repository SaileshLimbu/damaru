package com.powersoft.damaruadmin.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.damaruadmin.R
import com.powersoft.damaruadmin.databinding.ActivityAddEmulatorBinding
import com.powersoft.damaruadmin.viewmodels.AddEmulatorViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddEmulatorActivity @Inject constructor() : BaseActivity() {
    private val viewModel: AddEmulatorViewModel by viewModels()
    private lateinit var binding: ActivityAddEmulatorBinding

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEmulatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (intent.hasExtra("user")) {
//            user = gson.fromJson(intent.getStringExtra("user"), UserEntity::class.java)
//            binding.etName.setText(user?.name)
//            binding.etEmail.setText(user?.email)
//            binding.btnSubmit.text = getString(com.powersoft.common.R.string.update)
//            binding.title.text = getString(com.powersoft.common.R.string.update_account)
//            binding.passwordInputLayout.visibility(false)
//            binding.confirmPasswordInputLayout.visibility(false)
//        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            if (binding.etName.text.toString().isEmpty()) {
                AlertHelper.showToast(applicationContext, getString(R.string.please_enter_emulator_name))
            } else if (binding.etId.text.toString().isEmpty()) {
                AlertHelper.showToast(applicationContext, getString(R.string.please_enter_emulator_id))
            } else {
//            if (user != null) {
//                vm.editUser(user?.id!!, binding.etName.text.toString(), binding.etEmail.text.toString(),
//                    object : ResponseCallback {
//                        override fun onResponse(any: Any, errorMessage: String?) {
//                            if (errorMessage != null) {
//                                AlertHelper.showAlertDialog(this@AddUserActivity, getString(R.string.error), errorMessage)
//                            } else {
//                                setResult(RESULT_OK, Intent().putExtra("edited_name", binding.etName.text.toString())
//                                    .putExtra("edited_email", binding.etEmail.text.toString()))
//                                finish()
//                            }
//                        }
//                    })
//            } else {
                viewModel.addEmulator(binding.etName.text.toString(), binding.etId.text.toString(), binding.etDetails.text.toString(),
                    object : ResponseCallback {
                        override fun onResponse(any: Any, errorMessage: String?) {
                            if (errorMessage != null) {
                                AlertHelper.showAlertDialog(this@AddEmulatorActivity, getString(R.string.error), errorMessage)
                            } else {
                                setResult(RESULT_OK)
                                finish()
                            }
                        }
                    })
//            }
            }
        }
    }
}