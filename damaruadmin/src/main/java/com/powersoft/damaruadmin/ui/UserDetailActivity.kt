package com.powersoft.damaruadmin.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import com.powersoft.common.model.UserEntity
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.ui.PickerActivity
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.hide
import com.powersoft.common.utils.show
import com.powersoft.damaruadmin.R
import com.powersoft.damaruadmin.databinding.ActivityUserDetailBinding
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
//                if (result.data?.hasExtra("edited_name") == true) {
//                    binding.tvAccountName.text = result.data?.getStringExtra("edited_name")
//                } else if (result.data?.hasExtra("pin") == true) {
//                    binding.tvPin.text = result.data?.getStringExtra("pin")
//                }
            }
        }

    private val linkDeviceResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val items: ArrayList<PickerEntity>? = result.data?.getParcelableArrayListExtra(PickerActivity.Companion.EXTRA_SELECTED_ITEMS)
                if (items?.isNotEmpty() == true) {
//                    vm.linkDevices(items.map { pickerEntity ->
//                        gson.fromJson(pickerEntity.dataJson, DeviceEntity::class.java).deviceId
//                    }.toList(), userRepo.seasonEntity.value?.userId.toString(), account.id, object : ResponseCallback {
//                        override fun onResponse(any: Any, errorMessage: String?) {
//                            vm.getLinkedDevices(account.id)
//                            if (errorMessage != null) {
//                                AlertHelper.showAlertDialog(this@UserDetailActivity, title = getString(R.string.error), message = errorMessage)
//                            } else {
//                                AlertHelper.showSnackbar(binding.root, getString(R.string.linked_success))
//                            }
//                        }
//                    })
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

        binding.btnDelete.setOnClickListener {
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

        vm.allDevices.observe(this) {
            when (it) {
                is ResponseWrapper.Success -> {
                    deviceAdapter.submitList(it.data)
                    binding.loader.root.hide()
                    binding.errorView.root.hide()
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
            }
        }, DeviceListAdapter.Companion.TYPE.LIST)
    }

    private fun openPicker() {
//        PickerActivity.Companion.startForResult(
//            this@UserDetailActivity, vm.getFilteredList(),
//            true, linkDeviceResultLauncher
//        )
    }
}