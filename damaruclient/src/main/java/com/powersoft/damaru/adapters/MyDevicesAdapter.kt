package com.powersoft.damaru.adapters

import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.powersoft.damaru.R
import com.powersoft.damaru.databinding.ItemDeviceBinding
import com.powersoft.damaru.models.Device
import com.powersoft.damaru.ui.DeviceControlActivity

class MyDevicesAdapter(
    private val deviceList: List<Device>
) : RecyclerView.Adapter<MyDevicesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DeviceControlActivity::class.java)
                    .putExtra(DeviceControlActivity.CLIENT_ID, "2")
                    .putExtra(DeviceControlActivity.DEVICE_ID, deviceList[layoutPosition].deviceId)
                binding.root.context.startActivity(intent)
            }
        }

        fun bind(device: Device) {
            binding.apply {
                tvDeviceName.text = device.name
                imgDevice.setImageResource(device.image)
                tvRemainingDays.text = "${device.remainingDays} days"

                val colorRed = ContextCompat.getColor(itemView.context, R.color.red)
                tvRemainingDays.backgroundTintList = when (device.remainingDays) {
                    in 1..3, 0 -> ColorStateList.valueOf(colorRed)
                    else -> null // Default color or no tint
                }

                tvExpired.visibility = if (device.remainingDays == 0) View.VISIBLE else View.GONE
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
