package com.powersoft.damaru.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.listeners.RecyclerViewItemClickListener
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
import com.powersoft.damaru.R
import com.powersoft.damaru.adapters.MyDevicesAdapter
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
                Logg.d("Fuck there  >>>>> ${items?.map { pickerEntity ->
                    gson.fromJson(pickerEntity.dataJson, DeviceEntity::class.java).deviceId.toString()
                }}")
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
        if (userRepo.seasonEntity.value?.isRootUser == true || account.isAdmin == true) {
            binding.lvlPin.visibility = View.VISIBLE
            binding.tvPin.visibility = View.VISIBLE
        } else {
            binding.btnDelete.visibility = View.GONE
            binding.lvlPin.visibility = View.GONE
            binding.tvPin.visibility = View.GONE
        }
        if (userRepo.seasonEntity.value?.isRootUser == true && account.isAdmin == false) {
            binding.btnDelete.visibility = View.VISIBLE
        } else {
            binding.btnDelete.visibility = View.GONE
        }
        if (userRepo.seasonEntity.value?.accountId == account.id || userRepo.seasonEntity.value?.isRootUser == true || account.isAdmin == true) {
            binding.btnChangePin.visibility = View.VISIBLE
            binding.imgEdit.visibility = View.VISIBLE
        } else {
            binding.btnChangePin.visibility = View.GONE
            binding.imgEdit.visibility = View.GONE
        }
        if (account.isAdmin == true) {
            binding.holderAdminAccount.visibility = View.VISIBLE
        } else {
            binding.holderAdminAccount.visibility = View.GONE
        }

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
                    val deviceAdapter = MyDevicesAdapter(it.data, object : RecyclerViewItemClickListener<DeviceEntity> {
                        override fun onItemClick(viewId: Int, position: Int, data: DeviceEntity) {
//                            if (userRepo.seasonEntity.value?.isRootUser == true) {
//                                val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AccountDetailActivity)
//                                dialog.setTitle("Options")
//                                dialog.setItems(arrayOf("Connect to device", "Device Details")) { dialogInterface, itemPos ->
//                                    dialogInterface.dismiss()
//                                    when (itemPos) {
//                                        0 -> {
//                                            val intent = Intent(applicationContext, DeviceControlActivity::class.java)
//                                            startActivity(intent)
//                                        }
//
//                                        else -> {
//                                            startActivity(Intent(applicationContext, DeviceDetailsActivity::class.java).putExtra("device", Gson().toJson(data)))
//                                        }
//                                    }
//                                }
//                                dialog.show()
//                            } else {
//                                val intent = Intent(applicationContext, DeviceControlActivity::class.java)
////                                    .putExtra(DeviceControlActivity.USER_NAME, "theone")
////                                    .putExtra(DeviceControlActivity.TARGET_USER_NAME, data.deviceId)
//                                startActivity(intent)
//                            }
                        }

                    })
                    binding.recyclerView.adapter = deviceAdapter

                    binding.loader.root.visibility = View.GONE
                    binding.errorView.root.visibility = View.GONE
                }

                is ResponseWrapper.Error -> {
                    binding.loader.root.visibility = View.GONE
                    binding.errorView.tvError.text = it.errorResponse.message?.message
                    binding.errorView.root.visibility = View.VISIBLE
                }

                is ResponseWrapper.Loading -> {
                    binding.loader.root.visibility = View.VISIBLE
                    binding.errorView.root.visibility = View.GONE
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