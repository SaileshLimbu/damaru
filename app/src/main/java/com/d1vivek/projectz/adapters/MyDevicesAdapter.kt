package com.d1vivek.projectz.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.d1vivek.projectz.DeviceUsersActivity
import com.d1vivek.projectz.databinding.ItemMyDevicesBinding

class MyDevicesAdapter(private val data: List<String>) : RecyclerView.Adapter<MyDevicesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val b = ItemMyDevicesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(b)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(b: ItemMyDevicesBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.root.setOnClickListener {
                b.root.context.startActivity(Intent(b.root.context, DeviceUsersActivity::class.java))
            }
        }
    }
}
