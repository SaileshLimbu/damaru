package com.powersoft.damaru.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.utils.Logg
import com.powersoft.damaru.adapters.AccountsAdapter
import com.powersoft.damaru.databinding.ActivitDeviceDetailsBinding
import com.powersoft.damaru.viewmodels.DeviceDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceDetailsActivity : BaseActivity() {
    private val vm: DeviceDetailsViewModel by viewModels()
    private lateinit var binding: ActivitDeviceDetailsBinding

    override fun getViewModel(): BaseViewModel {
        return vm
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
            startActivity(Intent(applicationContext, AccountUsersActivity::class.java))
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DeviceDetailsActivity)
        }

        vm.allAccounts.observe(this) {
            when (it) {
                is ResponseWrapper.Success -> {
                    val accountAdapter = AccountsAdapter(it.data, object : RecyclerViewItemClickListener<AccountEntity> {
                        override fun onItemClick(position: Int, data: AccountEntity) {
                        }

                    })
                    binding.recyclerView.adapter = accountAdapter

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

        //set device info
        Logg.e("FUck you  ------- ${intent.getStringExtra("device")}")
        val deviceEntity : DeviceEntity = Gson().fromJson(intent.getStringExtra("device"), DeviceEntity::class.java)
        deviceEntity.let {
            Logg.d("FUCKKKKKKKKKKKKKKKKKKKKK >>>>>>>>>>>>> ${it.deviceName}")
            Logg.d("FUCKKKKKKKKKKKKKKKKKKKKK >>>>>>>>>>>>> ${binding.tvDeviceName}")
            binding.tvDeviceName.text = "fff ${it.deviceName}"
            binding.tvCreatedAt.text = "Subscribed on : ${it.createdAt}"
            binding.tvExpiresIn.text = "Expires In : ${it.expiresAt}"
        }
    }
}