package com.powersoft.damaru.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.powersoft.common.adapter.DeviceListAdapter
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
import com.powersoft.common.utils.visibility
import com.powersoft.damaru.R
import com.powersoft.damaru.databinding.ActivityAccountDetailBinding
import com.powersoft.damaru.viewmodels.AccountDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityAccountDetailBinding
    private val vm: AccountDetailViewModel by viewModels()
    private lateinit var account: AccountEntity

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var userRepo: UserRepo

    private val changePinResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                setResult(RESULT_OK)
                if (result.data?.hasExtra("edited_name") == true) {
                    binding.tvAccountName.text = result.data?.getStringExtra("edited_name")
                } else if (result.data?.hasExtra("pin") == true) {
                    binding.tvPin.text = result.data?.getStringExtra("pin")
                }
            }
        }

    private val linkDeviceResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val items: ArrayList<PickerEntity>? = result.data?.getParcelableArrayListExtra(PickerActivity.Companion.EXTRA_SELECTED_ITEMS)
                if (items?.isNotEmpty() == true) {
                    vm.linkDevices(items.map { pickerEntity ->
                        gson.fromJson(pickerEntity.dataJson, DeviceEntity::class.java).deviceId
                    }.toList(), userRepo.seasonEntity.value?.userId.toString(), account.id, object : ResponseCallback {
                        override fun onResponse(any: Any, errorMessage: String?) {
                            vm.getLinkedDevices(account.id)
                            if (errorMessage != null) {
                                AlertHelper.showAlertDialog(this@AccountDetailActivity, title = getString(R.string.error), message = errorMessage)
                            } else {
                                AlertHelper.showSnackbar(binding.root, getString(R.string.linked_success))
                            }
                        }
                    })
                }
            }
        }

    override fun getViewModel(): BaseViewModel {
        return vm
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        account = gson.fromJson(intent.getStringExtra("account"), AccountEntity::class.java)
        binding.tvAccountName.text = account.accountName
        binding.tvPin.text = account.pin
        binding.tvCreatedAt.text = getString(R.string.created_at, account.createdAt)

        val isRootUserOrAdmin = userRepo.seasonEntity.value?.isRootUser == true || account.isAdmin
        binding.lvlPin.visibility(isRootUserOrAdmin)
        binding.tvPin.visibility(isRootUserOrAdmin)
        binding.btnDelete.visibility(userRepo.seasonEntity.value?.isRootUser == true && !account.isAdmin)

        val isMyAccountOrRootAccountOrAdmin = userRepo.seasonEntity.value?.accountId == account.id ||
                userRepo.seasonEntity.value?.isRootUser == true ||
                account.isAdmin
        binding.btnChangePin.visibility(isMyAccountOrRootAccountOrAdmin)
        binding.imgEdit.visibility(isMyAccountOrRootAccountOrAdmin)

        binding.holderAdminAccount.visibility(account.isAdmin)

        binding.btnDelete.setOnClickListener {
            vm.deleteAccount(account.id, object : ResponseCallback {
                override fun onResponse(any: Any, errorMessage: String?) {
                    if (errorMessage == null) {
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        AlertHelper.showAlertDialog(
                            this@AccountDetailActivity, getString(R.string.error),
                            message = errorMessage,
                        )
                    }
                }
            })
        }

        binding.imgEdit.setOnClickListener {
            changePinResultLauncher.launch(Intent(applicationContext, AddAccountActivity::class.java).putExtra("account", gson.toJson(account)))
        }

        binding.btnChangePin.setOnClickListener {
            changePinResultLauncher.launch(Intent(applicationContext, ChangePinActivity::class.java).putExtra("account", gson.toJson(account)))
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
            if (vm.allDevices.value is ResponseWrapper.Success && (vm.allDevices.value as ResponseWrapper.Success).data.isNotEmpty()) {
                openPicker()
            } else {
                vm.getAllDevices()
            }
        }

        binding.btnDelete.setOnClickListener {
            AlertHelper.showAlertDialog(
                this@AccountDetailActivity, title = getString(R.string.delete_account) + " ??",
                message = getString(R.string.are_you_sure_you_want_to_delete_this_account),
                positiveButtonText = getString(R.string.delete),
                negativeButtonText = getString(com.powersoft.common.R.string.cancle),
                onPositiveButtonClick = {
                    vm.deleteAccount(account.id, object : ResponseCallback {
                        override fun onResponse(any: Any, errorMessage: String?) {
                            if (errorMessage != null) {
                                AlertHelper.showAlertDialog(
                                    this@AccountDetailActivity, getString(R.string.error), errorMessage
                                )
                            } else {
                                setResult(RESULT_OK)
                                finish()
                            }
                        }
                    })
                }
            )
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
        }

        vm.allLinkedDevices.observe(this) {
            when (it) {
                is ResponseWrapper.Success -> {
                    val deviceAdapter = DeviceListAdapter(object : RecyclerViewItemClickListener<DeviceEntity> {
                        override fun onItemClick(viewId: Int, position: Int, data: DeviceEntity) {
                            val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AccountDetailActivity)
                            dialog.setTitle("Options")
                            dialog.setItems(arrayOf("Unlink Device", "Device Logs")) { dialogInterface, itemPos ->
                                dialogInterface.dismiss()
                                when (itemPos) {
                                    0 -> {
                                        AlertHelper.showAlertDialog(
                                            this@AccountDetailActivity, title = getString(R.string.unlink_this_device),
                                            message = getString(R.string.are_you_sure_unlink_device),
                                            positiveButtonText = getString(R.string.delete),
                                            negativeButtonText = getString(com.powersoft.common.R.string.cancle),
                                            onPositiveButtonClick = {
                                                vm.unlinkDevice(data.deviceId, userRepo.seasonEntity.value?.userId.toString(), listOf(account.id),
                                                    object : ResponseCallback {
                                                        override fun onResponse(any: Any, errorMessage: String?) {
                                                            if (errorMessage != null) {
                                                                AlertHelper.showAlertDialog(
                                                                    this@AccountDetailActivity, getString(R.string.error), errorMessage,
                                                                )
                                                            } else {
                                                                setResult(RESULT_OK)
                                                                vm.getLinkedDevices(account.id)
                                                            }
                                                        }
                                                    })
                                            }
                                        )
                                    }

                                    else -> {
                                        LogsActivity.start(this@AccountDetailActivity, account.id, data.deviceId)
                                    }
                                }
                            }
                            dialog.show()
                        }

                    })
                    deviceAdapter.submitList(it.data)
                    binding.recyclerView.adapter = deviceAdapter

                    binding.loader.root.hide()
                    binding.errorView.root.hide()
                    binding.extendedFAB.show()
                }

                is ResponseWrapper.Error -> {
                    binding.loader.root.hide()
                    binding.errorView.tvError.text = it.message
                    binding.errorView.root.show()
                    binding.extendedFAB.show()
                }

                is ResponseWrapper.Loading -> {
                    binding.loader.root.show()
                    binding.errorView.root.hide()
                }
            }
        }

        vm.allDevices.observe(this) {
            when (it) {
                is ResponseWrapper.Success -> {
                    openPicker()
                }

                is ResponseWrapper.Error -> {
                    AlertHelper.showAlertDialog(
                        this@AccountDetailActivity, title = getString(R.string.error),
                        message = it.message
                    )
                }

                is ResponseWrapper.Loading -> {

                }
            }
        }

        vm.getLinkedDevices(account.id)
    }

    private fun openPicker() {
        PickerActivity.Companion.startForResult(
            this@AccountDetailActivity, vm.getFilteredList(),
            true, linkDeviceResultLauncher
        )
    }
}