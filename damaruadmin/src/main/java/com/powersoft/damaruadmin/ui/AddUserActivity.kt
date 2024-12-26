package com.powersoft.damaruadmin.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.damaruadmin.databinding.ActivityAddUserBinding
import com.powersoft.damaruadmin.viewmodels.AddUserViewModel

class AddUserActivity : BaseActivity() {
    private val vm : AddUserViewModel by viewModels()
    private lateinit var binding : ActivityAddUserBinding

    override fun getViewModel(): BaseViewModel {
        return vm
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            vm.addUser(binding.etName.text.toString(), binding.etEmail.text.toString(), binding.etPassword.text.toString(), binding.etConfirmPassword.text.toString())
        }
    }
}