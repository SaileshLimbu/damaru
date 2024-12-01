package com.d1vivek.projectz.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.d1vivek.projectz.DeviceUsersActivity
import com.d1vivek.projectz.R
import com.d1vivek.projectz.adapters.MyDevicesAdapter
import com.d1vivek.projectz.databinding.FragmentHomeBinding
import com.d1vivek.projectz.utils.GridSpacingItemDecoration

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val b get() = _binding!!

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

        val layoutManager = GridLayoutManager(requireContext(), 2)

        val spacing = resources.getDimensionPixelSize(R.dimen.grid_item_spacing)
        b.recyclerView.addItemDecoration(GridSpacingItemDecoration(spacing))

        b.recyclerView.layoutManager = layoutManager
        b.recyclerView.adapter = MyDevicesAdapter(getDummyData())

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
            startActivity(Intent(context, DeviceUsersActivity::class.java))
        }

    }

    private fun getDummyData(): List<String> {
        return List(7) { "Item ${it + 1}" }
    }
}