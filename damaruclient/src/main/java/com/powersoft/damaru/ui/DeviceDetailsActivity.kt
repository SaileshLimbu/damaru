package com.powersoft.damaru.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.DeviceEntity
import com.powersoft.damaru.databinding.ActivitDeviceDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceDetailsActivity : BaseActivity() {
//    private val vm: DeviceDetailsViewModel by viewModels()
    private lateinit var binding: ActivitDeviceDetailsBinding
    private val startActivityForResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
//                vm.getAllAccounts()
            }
        }

    override fun getViewModel(): BaseViewModel? {
        return null
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitDeviceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            finish()
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
            startActivityForResultLauncher.launch(Intent(applicationContext, AddAccountActivity::class.java))
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DeviceDetailsActivity)
        }

//        vm.allAccounts.observe(this) {
//            when (it) {
//                is ResponseWrapper.Success -> {
//                    val accountAdapter = AccountsAdapter(it.data, object : RecyclerViewItemClickListener<AccountEntity> {
//                        override fun onItemClick(position: Int, data: AccountEntity) {
//
//                            AlertHelper.showAlertDialog(
//                                this@DeviceDetailsActivity, title = getString(R.string.delete_account),
//                                message = getString(R.string.are_you_sure_you_want_to_delete_this_account),
//                                positiveButtonText = getString(R.string.delete),
//                                negativeButtonText = getString(R.string.cancle),
//                                onPositiveButtonClick = {
//                                    vm.deleteAccount(data.id!!, object : ResponseCallback {
//                                        override fun onResponse(any: Any, errorResponse: ErrorResponse?) {
//                                            AlertHelper.showAlertDialog(
//                                                this@DeviceDetailsActivity, title = errorResponse?.message?.error ?: getString(R.string.error),
//                                                message = errorResponse?.message?.message ?: getString(R.string.error),
//                                            )
//                                        }
//                                    })
//                                }
//                            )
//                        }
//                    })
//                    binding.tvTotalAccounts.text = it.data.size.toString()
//                    binding.recyclerView.adapter = accountAdapter
//
//                    binding.loader.root.visibility = View.GONE
//                    binding.errorView.root.visibility = View.GONE
//                }
//
//                is ResponseWrapper.Error -> {
//                    binding.loader.root.visibility = View.GONE
//                    binding.errorView.tvError.text = it.errorResponse.message?.message
//                    binding.errorView.root.visibility = View.VISIBLE
//                }
//
//                is ResponseWrapper.Loading -> {
//                    binding.loader.root.visibility = View.VISIBLE
//                    binding.errorView.root.visibility = View.GONE
//                }
//            }
//        }

        //set device info
        val deviceEntity: DeviceEntity = Gson().fromJson(intent.getStringExtra("device"), DeviceEntity::class.java)
        deviceEntity.let {
            binding.holderDevice.tvRemainingDays.text = "${deviceEntity.expiresAt} days"

            binding.mynametv.text = it.deviceName
            binding.tvCreatedAt.text = "Subscribed on : ${it.createdAt}"
            binding.tvExpiresIn.text = "Expires In : ${it.expiresAt}"
        }
    }
}