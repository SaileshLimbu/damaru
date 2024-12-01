package com.d1vivek.projectz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.d1vivek.projectz.R
import com.d1vivek.projectz.adapters.MyDevicesAdapter
import com.d1vivek.projectz.databinding.FragmentHomeBinding
import com.d1vivek.projectz.databinding.FragmentMoreBinding

class MoreFragment : Fragment(R.layout.fragment_more) {
    private var _binding: FragmentMoreBinding? = null
    private val b get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}