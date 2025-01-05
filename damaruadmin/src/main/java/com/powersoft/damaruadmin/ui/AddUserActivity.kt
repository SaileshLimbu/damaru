package com.powersoft.damaruadmin.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.damaruadmin.R
import com.powersoft.damaruadmin.databinding.ActivityAddUserBinding
import com.powersoft.damaruadmin.viewmodels.AddUserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddUserActivity : BaseActivity() {
    private val vm: AddUserViewModel by viewModels()
    private lateinit var binding: ActivityAddUserBinding

    override fun getViewModel(): BaseViewModel {
        return vm
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            vm.addUser(binding.etName.text.toString(), binding.etEmail.text.toString(), binding.etPassword.text.toString(), binding.etConfirmPassword.text.toString(),
                object : ResponseCallback {
                    override fun onResponse(any: Any, errorMessage: String?) {
                        if (errorMessage != null) {
                            AlertHelper.showAlertDialog(this@AddUserActivity, getString(R.string.error), errorMessage)
                        } else {
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                })
        }
    }
}