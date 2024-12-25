package com.powersoft.damaru.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.damaru.ui.AccountUsersActivity
import com.powersoft.damaru.R
import com.powersoft.damaru.adapters.MyDevicesAdapter
import com.powersoft.damaru.databinding.FragmentHomeBinding
import com.powersoft.damaru.models.Device
import com.powersoft.damaru.viewmodels.HomeViewmodel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val b get() = _binding!!
    private lateinit var deviceAdapter: MyDevicesAdapter
    private val vm : HomeViewmodel by viewModels()

    private val dummyDevices = listOf(
        Device("test-emulator",  "Samsung Galaxy s20 Ultra", R.drawable.screenshot1, 28),
        Device("test-emulator",  "Xiomi Redmi Note 8", R.drawable.screenshot2, 28),
        Device("test-emulator",  "Motorola Xr 250", R.drawable.screenshot3, 15),
        Device("test-emulator",  "Nokia 2200", R.drawable.screenshot1, 0),
        Device("test-emulator",  "Motorola GT", R.drawable.screenshot3, 3),
        Device("test-emulator",  "Samsung Galaxy s22", R.drawable.screenshot1, 5),
        Device("test-emulator",  "Xiomi Redmi Note 9 Pro", R.drawable.screenshot2, 7)
    )

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

        b.extendedFAB.setOnClickListener {
            startActivity(Intent(context, AccountUsersActivity::class.java))
        }

        vm.allDevices.observe(viewLifecycleOwner){
            when (it){
                is ResponseWrapper.Success -> {
                    deviceAdapter = MyDevicesAdapter(it.data)
                    b.recyclerView.adapter = deviceAdapter
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

    }
}