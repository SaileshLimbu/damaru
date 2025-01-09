package com.powersoft.damaruadmin.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.powersoft.common.adapter.DeviceListAdapter
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.hide
import com.powersoft.common.utils.show
import com.powersoft.damaruadmin.R
import com.powersoft.damaruadmin.databinding.FragmentDevicesBinding
import com.powersoft.damaruadmin.ui.AddEmulatorActivity
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

    private val addDeviceForResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            vm.getAllDevices()
        }
    }

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

        b.etSearch.addTextChangedListener {
            deviceAdapter.filter(it.toString())
        }

        b.extendedFAB.setOnClickListener {
            addDeviceForResultLauncher.launch(Intent(context, AddEmulatorActivity::class.java))
        }

        b.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    b.extendedFAB.shrink()
                } else if (dy < 0) {
                    b.extendedFAB.extend()
                }
            }
        })

        vm.allDevices.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    deviceAdapter.submitOriginalList(it.data)
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
                AlertHelper.showAlertDialog(requireContext(), title = getString(R.string.delete_emulator) + " ??",
                    message = getString(R.string.are_you_sure_you_want_to_delete_this_emulator),
                    positiveButtonText = getString(com.powersoft.common.R.string.delete),
                    negativeButtonText = getString(com.powersoft.common.R.string.cancle), onPositiveButtonClick = {
                        vm.deleteUser(data.deviceId, object : ResponseCallback {
                            override fun onResponse(any: Any, errorMessage: String?) {
                                if (errorMessage != null) {
                                    AlertHelper.showAlertDialog(requireActivity(), getString(R.string.error), errorMessage)
                                } else {
                                    deviceAdapter.removeItem(position)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        if (deviceAdapter.currentList.isEmpty()) {
                                            b.errorView.tvError.text = getString(R.string.no_emulators)
                                            b.errorView.root.show()
                                        }
                                    }, 300)
                                }
                            }
                        })
                    })
            }
        }, DeviceListAdapter.Companion.TYPE.LIST, shouldShowStatus = true)
    }

    override fun onItemClick(viewId: Int, position: Int, data: DeviceEntity) {

    }
}