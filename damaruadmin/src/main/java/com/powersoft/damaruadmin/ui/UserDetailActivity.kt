package com.powersoft.damaruadmin.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.damaruadmin.databinding.ActivityUserDetailBinding
import com.powersoft.damaruadmin.viewmodels.UserDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailActivity : BaseActivity() {
    private val viewModel: UserDetailViewModel by viewModels()
    private lateinit var binding: ActivityUserDetailBinding

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}