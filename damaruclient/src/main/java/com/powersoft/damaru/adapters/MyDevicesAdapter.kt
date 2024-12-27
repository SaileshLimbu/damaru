package com.powersoft.damaru.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.DeviceEntity
import com.powersoft.damaru.R
import com.powersoft.damaru.databinding.ItemDeviceBinding

class MyDevicesAdapter(
    private val deviceList: List<DeviceEntity>,
    private val clickListener : RecyclerViewItemClickListener<DeviceEntity>
) : RecyclerView.Adapter<MyDevicesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                clickListener.onItemClick(layoutPosition, deviceList[layoutPosition])
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(device: DeviceEntity) {
            binding.apply {
                tvDeviceName.text = device.deviceName
                Glide.with(itemView.context).load(device.screenshot).into(imgDevice)
                tvRemainingDays.text = "${device.expiresAt} days"

                val colorRed = ContextCompat.getColor(itemView.context, R.color.red)
                tvRemainingDays.backgroundTintList = when  {
                    ((device.expiresAt?.toIntOrNull()) ?: 0) < 1 -> ColorStateList.valueOf(colorRed)
                    else -> null
                }

                tvExpired.visibility = if (((device.expiresAt?.toIntOrNull()) ?: 0) < 1) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = deviceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deviceList[position])
    }
}
