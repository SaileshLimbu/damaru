package com.powersoft.damaruadmin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.powersoft.common.adapter.DeviceListAdapter
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.utils.hide
import com.powersoft.common.utils.show
import com.powersoft.damaruadmin.R
import com.powersoft.damaruadmin.databinding.FragmentDevicesBinding
import com.powersoft.damaruadmin.viewmodels.AdminDeviceFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminDevicesFragment : Fragment(R.layout.fragment_devices), RecyclerViewItemClickListener<DeviceEntity> {
    private var _binding: FragmentDevicesBinding? = null
    private val b get() = _binding!!
    private val deviceAdapter by lazy { createDeviceAdapter() }
    private val vm: AdminDeviceFragmentViewModel by viewModels()

    @Inject
    lateinit var userRepo: UserRepo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDevicesBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.swipeRefresh.setOnRefreshListener {
            vm.getAllDevices()
        }

        b.recyclerView.layoutManager = LinearLayoutManager(activity)
        b.recyclerView.adapter = deviceAdapter

        vm.allDevices.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    deviceAdapter.submitList(it.data)
                    b.loader.root.hide()
                    b.errorView.root.hide()

                    b.swipeRefresh.isRefreshing = false
                }

                is ResponseWrapper.Error -> {
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
    }

    private fun createDeviceAdapter(): DeviceListAdapter {
        return DeviceListAdapter(object : RecyclerViewItemClickListener<DeviceEntity> {
            override fun onItemClick(viewId: Int, position: Int, data: DeviceEntity) {
            }
        }, DeviceListAdapter.Companion.TYPE.LIST, shouldShowStatus = true)
    }

    override fun onItemClick(viewId: Int, position: Int, data: DeviceEntity) {

    }
}