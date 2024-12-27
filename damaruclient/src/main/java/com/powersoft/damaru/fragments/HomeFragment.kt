package com.powersoft.damaru.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.utils.Logg
import com.powersoft.damaru.R
import com.powersoft.damaru.adapters.MyDevicesAdapter
import com.powersoft.damaru.databinding.FragmentHomeBinding
import com.powersoft.damaru.ui.DeviceControlActivity
import com.powersoft.damaru.ui.DeviceDetailsActivity
import com.powersoft.damaru.viewmodels.HomeViewmodel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val b get() = _binding!!
    private lateinit var deviceAdapter: MyDevicesAdapter
    private val vm: HomeViewmodel by viewModels()

    @Inject
    lateinit var userRepo: UserRepo


//    private val dummyDevices = listOf(
//        Device("test-emulator", "Samsung Galaxy s20 Ultra", R.drawable.screenshot1, 28),
//        Device("test-emulator", "Xiomi Redmi Note 8", R.drawable.screenshot2, 28),
//        Device("test-emulator", "Motorola Xr 250", R.drawable.screenshot3, 15),
//        Device("test-emulator", "Nokia 2200", R.drawable.screenshot1, 0),
//        Device("test-emulator", "Motorola GT", R.drawable.screenshot3, 3),
//        Device("test-emulator", "Samsung Galaxy s22", R.drawable.screenshot1, 5),
//        Device("test-emulator", "Xiomi Redmi Note 9 Pro", R.drawable.screenshot2, 7)
//    )

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

        b.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        vm.allDevices.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    deviceAdapter = MyDevicesAdapter(it.data, object : RecyclerViewItemClickListener<DeviceEntity> {
                        override fun onItemClick(position: Int, data: DeviceEntity) {
                            if (userRepo.seasonEntity.value?.isRootUser == true) {
                                val dialog: AlertDialog.Builder = AlertDialog.Builder(activity)
                                dialog.setItems(arrayOf("Connect to device", "Device Details")) { dialogInterface, itemPos ->
                                    dialogInterface.dismiss()
                                    when (itemPos) {
                                        0 -> {
                                            val intent = Intent(context, DeviceControlActivity::class.java)
                                                .putExtra(DeviceControlActivity.USER_NAME, "theone")
                                                .putExtra(DeviceControlActivity.TARGET_USER_NAME, data.deviceId)
                                            startActivity(intent)
                                        }

                                        else -> {
                                            Logg.e("FUck you  ------- ${Gson().toJson(data)}")
                                            startActivity(Intent(context, DeviceDetailsActivity::class.java).putExtra("device", Gson().toJson(data)))
                                        }
                                    }
                                }
                                dialog.show()
                            } else {
                                val intent = Intent(context, DeviceControlActivity::class.java)
                                    .putExtra(DeviceControlActivity.USER_NAME, "theone")
                                    .putExtra(DeviceControlActivity.TARGET_USER_NAME, data.deviceId)
                                startActivity(intent)
                            }
                        }

                    })
                    b.recyclerView.adapter = deviceAdapter

                    b.loader.root.visibility = View.GONE
                    b.errorView.root.visibility = View.GONE
                }

                is ResponseWrapper.Error -> {
                    b.loader.root.visibility = View.GONE
                    b.errorView.tvError.text = it.errorResponse.message?.message
                    b.errorView.root.visibility = View.VISIBLE
                }

                is ResponseWrapper.Loading -> {
                    b.loader.root.visibility = View.VISIBLE
                    b.errorView.root.visibility = View.GONE
                }
            }
        }

        b.swipeRefresh.setOnRefreshListener {
            vm.getMyEmulators()
        }

    }
}