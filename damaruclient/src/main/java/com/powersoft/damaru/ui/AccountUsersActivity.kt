package com.powersoft.damaru.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.damaru.R
import com.powersoft.damaru.adapters.DeviceUserAdapter
import com.powersoft.damaru.adapters.User
import com.powersoft.damaru.databinding.ActivityAccountUsersBinding
import com.powersoft.damaru.viewmodels.AccountUsersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountUsersActivity : BaseActivity() {
    private lateinit var b: ActivityAccountUsersBinding
    private val viewModel: AccountUsersViewModel by viewModels()

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityAccountUsersBinding.inflate(layoutInflater)
        setContentView(b.root)

        val users = listOf(
            User("John", R.drawable.phone_walpaper),
            User("Emily", R.drawable.phone_walpaper),
            User("Sarah", R.drawable.phone_walpaper),
            User("Michael", R.drawable.phone_walpaper),
            User("", -1)
        )

        val adapter = DeviceUserAdapter(users) { user ->
            if (user.name.isEmpty() && user.profileImage == -1) {
                startActivity(Intent(this@AccountUsersActivity, AddUserActivity::class.java))
            } else {
                startActivity(Intent(this@AccountUsersActivity, PinActivityImpl::class.java))
            }
        }

        b.userSelectionRecyclerView.layoutManager = GridLayoutManager(this, 3)
        b.userSelectionRecyclerView.adapter = adapter

        viewModel.liveData.observe(this) {
            when(it){
                is ResponseWrapper.Error -> {
                    AlertHelper.showSnackbar(b.root, it.errorResponse.message?.message ?: "")

                }
                is ResponseWrapper.Loading -> TODO()
                is ResponseWrapper.Success -> TODO()
            }
        }
        viewModel.getAccountUsers()

    }
}