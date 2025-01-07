package com.powersoft.damaru.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.ui.LogsActivity
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.hide
import com.powersoft.common.utils.show
import com.powersoft.damaru.R
import com.powersoft.damaru.adapters.AccountsAdapter
import com.powersoft.damaru.databinding.ActivitDeviceDetailsBinding
import com.powersoft.damaru.viewmodels.DeviceDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeviceDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivitDeviceDetailsBinding
    private val vm: DeviceDetailsViewModel by viewModels()
    private val accountsAdapter by lazy { createAccountAdapter() }

    @Inject
    lateinit var userRepo: UserRepo

//    private val startActivityForResultLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//            }
//        }

    override fun getViewModel(): BaseViewModel = vm

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitDeviceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DeviceDetailsActivity)
            adapter = accountsAdapter
        }

        //set device info
        val deviceEntity: DeviceEntity = Gson().fromJson(intent.getStringExtra("device"), DeviceEntity::class.java)
        deviceEntity.let {
            vm.deviceId = it.deviceId
            binding.holderDevice.tvRemainingDays.text = "${deviceEntity.expiresAt} days"
            binding.mynametv.text = it.deviceName
            binding.tvCreatedAt.text = "Subscribed on : ${it.createdAt}"
            binding.tvExpiresIn.text = "Expires In : ${it.expiresAt} days"

            vm.allAccounts.observe(this) { response ->
                when (response) {
                    is ResponseWrapper.Success -> {
                        accountsAdapter.submitList(response.data)
                        if (response.data.isNotEmpty()) {
                            binding.tvTotalAccounts.text = response.data.size.toString()
                            binding.holderAccounts.show()
                        }

                        binding.loader.root.hide()
                        binding.errorView.root.hide()
                    }

                    is ResponseWrapper.Error -> {
                        binding.loader.root.hide()
                        binding.errorView.tvError.text = response.message
                        binding.errorView.root.show()
                    }

                    is ResponseWrapper.Loading -> {
                        binding.loader.root.show()
                        binding.errorView.root.hide()
                    }
                }
            }

            vm.getLinkedAccounts(it.deviceId)
        }
    }

    private fun createAccountAdapter(): AccountsAdapter {
        return AccountsAdapter(AccountsAdapter.Companion.For.LINKED_ACCOUNTS, userRepo.seasonEntity.value?.isRootUser == true,
            object : RecyclerViewItemClickListener<AccountEntity> {
                override fun onItemClick(viewId: Int, position: Int, data: AccountEntity) {
                    if (viewId == R.id.imgDelete) {
                        AlertHelper.showAlertDialog(
                            this@DeviceDetailsActivity, title = getString(R.string.unlink_this_account),
                            message = getString(R.string.are_you_sure_unlink_account),
                            positiveButtonText = getString(R.string.delete),
                            negativeButtonText = getString(com.powersoft.common.R.string.cancle),
                            onPositiveButtonClick = {
                                vm.unlinkAccount(vm.deviceId, userRepo.seasonEntity.value?.userId.toString(), listOf(data.id),
                                    object : ResponseCallback {
                                        override fun onResponse(any: Any, errorMessage: String?) {
                                            if (errorMessage != null) {
                                                AlertHelper.showAlertDialog(
                                                    this@DeviceDetailsActivity, getString(R.string.error), errorMessage,
                                                )
                                            } else {
                                                accountsAdapter.removeItem(position)
                                            }
                                        }
                                    })
                            }
                        )
                    } else if (viewId == R.id.imgLogs) {
                        startActivity(Intent(this@DeviceDetailsActivity, LogsActivity::class.java))
                    }
                }
            })
    }
}