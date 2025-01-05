package com.powersoft.damaru.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.model.PickerEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.ui.PickerActivity
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.Logg
import com.powersoft.common.utils.hide
import com.powersoft.common.utils.show
import com.powersoft.common.utils.visibility
import com.powersoft.damaru.R
import com.powersoft.damaru.adapters.DeviceListAdapter
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
                }
            }
        }

    private val linkDeviceResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val items: ArrayList<PickerEntity>? = result.data?.getParcelableArrayListExtra<PickerEntity>(PickerActivity.Companion.EXTRA_SELECTED_ITEMS)
                Logg.d(
                    "Fuck there  >>>>> ${
                        items?.map { pickerEntity ->
                            gson.fromJson(pickerEntity.dataJson, DeviceEntity::class.java).deviceId.toString()
                        }
                    }"
                )
                if (items?.isNotEmpty() == false) {
                    vm.linkDevices(items.map { pickerEntity ->
                        gson.fromJson(pickerEntity.dataJson, DeviceEntity::class.java).deviceId.toString()
                    }.toList(), userRepo.seasonEntity.value?.accountId.toString(), account.id.toString(), object : ResponseCallback {
                        override fun onResponse(any: Any, errorResponse: ErrorResponse?) {
                            setResult(RESULT_OK)
                            if (errorResponse != null) {
                                AlertHelper.showAlertDialog(this@AccountDetailActivity, title = getString(R.string.error), message = errorResponse.message?.message ?: "")
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
            vm.deleteAccount(account.id!!, object : ResponseCallback {
                override fun onResponse(any: Any, errorResponse: ErrorResponse?) {
                    if (errorResponse == null) {
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        AlertHelper.showAlertDialog(
                            this@AccountDetailActivity, title = errorResponse.message?.error ?: getString(R.string.error),
                            message = errorResponse.message?.message ?: getString(R.string.error),
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
                negativeButtonText = getString(R.string.cancle),
                onPositiveButtonClick = {
                    vm.deleteAccount(account.id!!, object : ResponseCallback {
                        override fun onResponse(any: Any, errorResponse: ErrorResponse?) {
                            if (errorResponse != null) {
                                AlertHelper.showAlertDialog(
                                    this@AccountDetailActivity, title = errorResponse.message?.error ?: getString(R.string.error),
                                    message = errorResponse.message?.message ?: getString(R.string.error),
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
                    val deviceAdapter = DeviceListAdapter(null)
                    deviceAdapter.submitList(it.data)
                    binding.recyclerView.adapter = deviceAdapter

                    binding.loader.root.hide()
                    binding.errorView.root.hide()
                }

                is ResponseWrapper.Error -> {
                    binding.loader.root.hide()
                    binding.errorView.tvError.text = it.errorResponse.message?.message
                    binding.errorView.root.show()
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
                        message = it.errorResponse.message?.message ?: ""
                    )
                }

                is ResponseWrapper.Loading -> {

                }
            }
        }

        vm.getLinkedDevices(account.id.toString())
    }

    private fun openPicker() {
        PickerActivity.Companion.startForResult(
            this@AccountDetailActivity,
            (vm.allDevices.value as ResponseWrapper.Success).data.map {
                PickerEntity(it.deviceName.toString(), gson.toJson(it))
            }.toList(),
            true, linkDeviceResultLauncher
        )
    }
}