package com.powersoft.damaruadmin.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.powersoft.common.adapter.DeviceListAdapter
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.PickerEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.State
import com.powersoft.common.model.UserEntity
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.ui.PickerActivity
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.hide
import com.powersoft.common.utils.show
import com.powersoft.common.utils.visibility
import com.powersoft.damaruadmin.R
import com.powersoft.damaruadmin.databinding.ActivityUserDetailBinding
import com.powersoft.damaruadmin.databinding.AlertExtendBinding
import com.powersoft.damaruadmin.viewmodels.UserDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityUserDetailBinding
    private val vm: UserDetailViewModel by viewModels()
    private lateinit var user: UserEntity
    private val deviceAdapter by lazy { createDeviceAdapter() }

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var userRepo: UserRepo

    private val editUserResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                setResult(RESULT_OK)
                if (result.data?.hasExtra("edited_name") == true) {
                    binding.tvUserName.text = result.data?.getStringExtra("edited_name")
                }
                if (result.data?.hasExtra("edited_email") == true) {
                    binding.tvEmail.text = result.data?.getStringExtra("edited_email")
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
                    }.toList(), user.id, object : ResponseCallback {
                        override fun onResponse(any: Any, errorMessage: String?) {
                            if (errorMessage != null) {
                                AlertHelper.showAlertDialog(this@UserDetailActivity, title = getString(R.string.error), message = errorMessage)
                            } else {
                                setResult(RESULT_OK)
                                vm.getAllDevices()
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

        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        user = gson.fromJson(intent.getStringExtra("user"), UserEntity::class.java)
        binding.tvUserName.text = user.name
        binding.tvEmail.text = user.email
        binding.btnDelete.visibility(!user.isSuperAdmin)
        binding.btnEdit.visibility(!user.isSuperAdmin)
        binding.extendedFAB.visibility(!user.isSuperAdmin)

        binding.btnDelete.setOnClickListener {
            AlertHelper.showAlertDialog(this@UserDetailActivity, title = getString(R.string.delete_user) + " ??",
                message = getString(R.string.are_you_sure_you_want_to_delete_this_user),
                positiveButtonText = getString(com.powersoft.common.R.string.delete),
                negativeButtonText = getString(com.powersoft.common.R.string.cancle), onPositiveButtonClick = {
                    vm.deleteUser(user.id, object : ResponseCallback {
                        override fun onResponse(any: Any, errorMessage: String?) {
                            if (errorMessage == null) {
                                setResult(RESULT_OK)
                                finish()
                            } else {
                                AlertHelper.showAlertDialog(
                                    this@UserDetailActivity, getString(R.string.error),
                                    message = errorMessage,
                                )
                            }
                        }
                    })
                })
        }

        binding.btnEdit.setOnClickListener {
            editUserResultLauncher.launch(Intent(applicationContext, AddUserActivity::class.java).putExtra("user", gson.toJson(user)))
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = deviceAdapter

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
            openPicker()
        }

        vm.allDevices.observe(this) {
            when (it) {
                is ResponseWrapper.Success -> {
                    val mList = it.data.filter { device ->
                        device.userId == user.id
                    }
                    deviceAdapter.submitList(mList)
                    binding.loader.root.hide()
                    if (mList.isNotEmpty()) {
                        binding.errorView.root.hide()
                    } else {
                        binding.errorView.tvError.text = getString(R.string.no_devices_assigned)
                        binding.errorView.root.show()
                    }
                    binding.extendedFAB.show()
                }

                is ResponseWrapper.Error -> {
                    binding.loader.root.hide()
                    binding.errorView.tvError.text = it.message
                    binding.errorView.root.show()
                }

                is ResponseWrapper.Loading -> {
                    binding.loader.root.show()
                    binding.errorView.root.hide()
                }
            }
        }
    }

    private fun createDeviceAdapter(): DeviceListAdapter {
        return DeviceListAdapter(object : RecyclerViewItemClickListener<DeviceEntity> {
            override fun onItemClick(viewId: Int, position: Int, data: DeviceEntity) {
                if (viewId == R.id.btnDelete) {
                    AlertHelper.showAlertDialog(
                        this@UserDetailActivity, title = getString(R.string.unlink_this_device),
                        message = getString(R.string.are_you_sure_unlink_device),
                        positiveButtonText = getString(R.string.delete),
                        negativeButtonText = getString(com.powersoft.common.R.string.cancle),
                        onPositiveButtonClick = {
                            vm.unlinkDevice(user.id, listOf(data.deviceId),
                                object : ResponseCallback {
                                    override fun onResponse(any: Any, errorMessage: String?) {
                                        if (errorMessage != null) {
                                            AlertHelper.showAlertDialog(
                                                this@UserDetailActivity, getString(R.string.error), errorMessage,
                                            )
                                        } else {
                                            vm.getAllDevices()
                                        }
                                    }
                                })
                        }
                    )
                } else if (viewId == com.powersoft.common.R.id.btnExtend) {
                    showExtendAlert(data)
                }
            }
        }, DeviceListAdapter.Companion.TYPE.LIST)
    }

    private fun openPicker() {
        PickerActivity.Companion.startForResult(
            this@UserDetailActivity,
            (vm.allDevices.value as ResponseWrapper.Success).data
                .mapNotNull {
                    if (it.state == State.AVAILABLE)
                        PickerEntity(it.deviceName, gson.toJson(it))
                    else
                        null
                }, true, linkDeviceResultLauncher
        )
    }

    private fun showExtendAlert(device: DeviceEntity) {
        val alertDialog = AlertDialog.Builder(this@UserDetailActivity)
        val alertBinding = AlertExtendBinding.inflate(layoutInflater)
        alertDialog.setView(alertBinding.root)
        val dialog = alertDialog.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertBinding.btnClose2.setOnClickListener {
            dialog.dismiss()
        }

        alertBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        alertBinding.btnExtend.setOnClickListener {
            if (alertBinding.etDays.text.toString().isEmpty()) {
                AlertHelper.showToast(this@UserDetailActivity, getString(R.string.please_enter_no_of_days_to_extend))
                return@setOnClickListener
            }
            dialog.dismiss()
            vm.extendDeviceExpiry(user.id, device.deviceId, "30",
                object : ResponseCallback {
                    override fun onResponse(any: Any, errorMessage: String?) {
                        if (errorMessage != null) {
                            AlertHelper.showAlertDialog(
                                this@UserDetailActivity, getString(R.string.error), errorMessage,
                            )
                        } else {
                            vm.getAllDevices()
                        }
                    }
                })
        }
    }
}