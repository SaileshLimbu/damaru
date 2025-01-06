package com.powersoft.damaruadmin.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.google.gson.Gson
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.UserEntity
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.damaruadmin.R
import com.powersoft.damaruadmin.databinding.ActivityAddUserBinding
import com.powersoft.damaruadmin.viewmodels.AddUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddUserActivity : BaseActivity() {
    private val vm: AddUserViewModel by viewModels()
    private lateinit var binding: ActivityAddUserBinding

    @Inject
    lateinit var gson: Gson
    private var user: UserEntity? = null

    override fun getViewModel(): BaseViewModel {
        return vm
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("user")) {
            user = gson.fromJson(intent.getStringExtra("user"), UserEntity::class.java)
            binding.etName.setText(user?.name)
            binding.etEmail.setText(user?.email)
            binding.btnSubmit.text = getString(com.powersoft.common.R.string.update)
            binding.title.text = getString(com.powersoft.common.R.string.update_account)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            if (user != null) {
                vm.editUser(user?.id!!, binding.etName.text.toString(), binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(), binding.etConfirmPassword.text.toString(),
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
            } else {
                vm.addUser(binding.etName.text.toString(), binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(), binding.etConfirmPassword.text.toString(),
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
}