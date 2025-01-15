package com.powersoft.damaru.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.PickerEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.ui.LogsActivity
import com.powersoft.common.ui.PickerActivity
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
    lateinit var deviceEntity: DeviceEntity

    @Inject
    lateinit var userRepo: UserRepo

    @Inject
    lateinit var gson: Gson

    private val linkAccountResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val items: ArrayList<PickerEntity>? = result.data?.getParcelableArrayListExtra(PickerActivity.Companion.EXTRA_SELECTED_ITEMS)
                if (items?.isNotEmpty() == true) {
                    vm.linkAccounts(items.map { pickerEntity ->
                        gson.fromJson(pickerEntity.dataJson, AccountEntity::class.java).id
                    }.toList(), userRepo.seasonEntity.value?.userId.toString(), deviceEntity.deviceId, object : ResponseCallback {
                        override fun onResponse(any: Any, errorMessage: String?) {
                            vm.getLinkedAccounts(deviceEntity.deviceId)
                            if (errorMessage != null) {
                                AlertHelper.showAlertDialog(this@DeviceDetailsActivity, title = getString(R.string.error), message = errorMessage)
                            } else {
                                AlertHelper.showSnackbar(binding.root, getString(R.string.accounts_linked_success))
                            }
                        }
                    })
                }
            }
        }

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
        deviceEntity = Gson().fromJson(intent.getStringExtra("device"), DeviceEntity::class.java)

        vm.deviceId = deviceEntity.deviceId
        binding.holderDevice.tvRemainingDays.text = "${deviceEntity.expiresAt} days"
        binding.mynametv.text = deviceEntity.deviceName
        binding.tvCreatedAt.text = "Subscribed on : ${deviceEntity.createdAt}"
        binding.tvExpiresIn.text = "Expires In : ${deviceEntity.expiresAt} days"
        binding.noteDetail.setText(if (deviceEntity.details.isNullOrEmpty()) "" else deviceEntity.details)

        binding.btnEditDetails.setOnClickListener {
            if (binding.btnEditDetails.text == "SAVE") {
                vm.editDeviceDetails(binding.noteDetail.text.toString(), deviceEntity.deviceId, object : ResponseCallback {
                    override fun onResponse(any: Any, errorMessage: String?) {
                        binding.noteDetail.isEnabled = false
                        binding.btnEditDetails.text = "Edit"
                        binding.btnEditDetails.icon = ContextCompat.getDrawable(this@DeviceDetailsActivity, com.powersoft.common.R.drawable.ic_edit)
                        if (errorMessage != null) {
                            AlertHelper.showToast(this@DeviceDetailsActivity, errorMessage)
                        } else {
                            setResult(RESULT_OK)
                        }
                    }
                })
            } else {
                binding.noteDetail.isEnabled = true
                binding.noteDetail.requestFocus()
                binding.btnEditDetails.text = "SAVE"
                binding.btnEditDetails.icon = ContextCompat.getDrawable(this, R.drawable.ic_save)
            }
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.extendedFAB.shrink()
                } else if (dy < 0) {
                    binding.extendedFAB.extend()
                }
            }
        })

        binding.extendedFAB.setOnClickListener {
            if (vm.allAccounts.value is ResponseWrapper.Success && (vm.allAccounts.value as ResponseWrapper.Success).data.isNotEmpty()) {
                openPicker()
            } else {
                vm.getAllAccounts()
            }
        }

        vm.allAccounts.observe(this) {
            when (it) {
                is ResponseWrapper.Success -> {
                    openPicker()
                }

                is ResponseWrapper.Error -> {
                }

                is ResponseWrapper.Loading -> {}
            }
        }

        vm.allLinkedAccounts.observe(this) { response ->
            when (response) {
                is ResponseWrapper.Success -> {
                    accountsAdapter.submitList(response.data)
                    if (response.data.isNotEmpty()) {
                        binding.tvTotalAccounts.text = response.data.size.toString()
                        binding.holderAccounts.show()
                    }

                    binding.loader.root.hide()
                    binding.errorView.root.hide()
                    binding.extendedFAB.show()
                }

                is ResponseWrapper.Error -> {
                    binding.loader.root.hide()
                    binding.errorView.tvError.text = response.message
                    binding.errorView.root.show()
                    binding.extendedFAB.show()
                }

                is ResponseWrapper.Loading -> {
                    binding.loader.root.show()
                    binding.errorView.root.hide()
                }
            }
        }

        vm.getLinkedAccounts(deviceEntity.deviceId)
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
                                                vm.getLinkedAccounts(deviceEntity.deviceId)
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    if (accountsAdapter.currentList.isEmpty()) {
                                                        binding.errorView.tvError.text = getString(R.string.no_linked_accounts)
                                                        binding.errorView.root.show()
                                                    }
                                                }, 300)
                                            }
                                        }
                                    })
                            }
                        )
                    } else if (viewId == R.id.imgLogs) {
                        LogsActivity.start(this@DeviceDetailsActivity, data.id, deviceEntity.deviceId)
                    }
                }
            })
    }

    private fun openPicker() {
        PickerActivity.Companion.startForResult(
            this@DeviceDetailsActivity, vm.getFilteredList(),
            true, linkAccountResultLauncher
        )
    }
}