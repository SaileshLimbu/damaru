package com.powersoft.damaru.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.powersoft.common.adapter.DeviceListAdapter
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.OptionItem
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.Status
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.utils.AlertUtils
import com.powersoft.common.utils.hide
import com.powersoft.common.utils.show
import com.powersoft.damaru.R
import com.powersoft.damaru.databinding.FragmentHomeBinding
import com.powersoft.damaru.ui.DeviceControlActivity
import com.powersoft.damaru.ui.DeviceDetailsActivity
import com.powersoft.damaru.viewmodels.HomeViewmodel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), RecyclerViewItemClickListener<DeviceEntity> {
    private var _binding: FragmentHomeBinding? = null
    private val b get() = _binding!!
    private lateinit var deviceAdapter: DeviceListAdapter
    private val vm: HomeViewmodel by viewModels()

    @Inject
    lateinit var userRepo: UserRepo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deviceAdapter = DeviceListAdapter(this)

        b.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = deviceAdapter
        }

        vm.allDevices.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    deviceAdapter.submitList(it.data)
                    b.loader.root.hide()
                    b.errorView.root.hide()
                    b.swipeRefresh.isRefreshing = false
                }

                is ResponseWrapper.Error -> {
                    deviceAdapter.submitList(emptyList())
                    b.loader.root.hide()
                    b.errorView.tvError.text = it.message
                    b.errorView.root.show()

                    b.swipeRefresh.isRefreshing = false
                }

                is ResponseWrapper.Loading -> {
                    b.loader.root.show()
                    b.errorView.root.hide()
                }
            }
        }

        b.swipeRefresh.setOnRefreshListener { vm.getMyEmulators() }
    }

    override fun onResume() {
        super.onResume()
        vm.getMyEmulators()
    }

    override fun onItemClick(viewId: Int, position: Int, data: DeviceEntity) {
        val accountId = userRepo.seasonEntity.value?.accountId
        val token = userRepo.seasonEntity.value?.accessToken
        if (userRepo.seasonEntity.value?.isRootUser == true) {
            AlertUtils.showOptionDialog(activity, "Options", arrayOf(
                OptionItem("Connect to device", R.drawable.ic_connect_device),
                OptionItem("Device Details", R.drawable.ic_details))){
                when (it) {
                    0 -> {
                        if (data.status == Status.online) {
                            val intent = Intent(context, DeviceControlActivity::class.java)
                                .putExtra(DeviceControlActivity.CLIENT_ID, accountId.toString())
                                .putExtra(DeviceControlActivity.DEVICE_ID, data.deviceId)
                                .putExtra(DeviceControlActivity.TOKEN, token)
                            startActivity(intent)
                        } else {
                            AlertUtils.showMessage(requireActivity(), "Oops!!!", "Emulator is offline")
                        }
                    }

                    else -> {
                        startActivity(
                            Intent(context, DeviceDetailsActivity::class.java)
                                .putExtra("device", Gson().toJson(data))
                        )
                    }
                }
            }
        } else {
            if (data.status == Status.online) {
                val intent = Intent(context, DeviceControlActivity::class.java)
                    .putExtra(DeviceControlActivity.CLIENT_ID, accountId.toString())
                    .putExtra(DeviceControlActivity.DEVICE_ID, data.deviceId)
                    .putExtra(DeviceControlActivity.TOKEN, token)
                startActivity(intent)
            } else {
                AlertUtils.showMessage(requireActivity(), "Oops!!!", "Emulator is offline")
            }
        }
    }
}