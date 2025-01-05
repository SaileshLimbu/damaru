package com.powersoft.damaru.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.powersoft.common.databinding.ItemDeviceBinding
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.Status
import com.powersoft.damaru.R

class DeviceListAdapter(
    private val clickListener : RecyclerViewItemClickListener<DeviceEntity>? = null
) : ListAdapter<DeviceEntity, DeviceListAdapter.ViewHolder>(DeviceDiffCallback()) {

    inner class ViewHolder(private val binding: ItemDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                clickListener?.onItemClick(it.id, layoutPosition, getItem(layoutPosition))
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

                val colorStateList : ColorStateList = if (device.status == Status.online){
                    ColorStateList.valueOf(ContextCompat.getColor(itemView.context, com.powersoft.common.R.color.greenLight))
                }else{
                    ColorStateList.valueOf(ContextCompat.getColor(itemView.context, com.powersoft.common.R.color.primary_light))
                }
                imgStatus.imageTintList = colorStateList
                tvStatus.text = device.status.name.capitalizeFirstLetter()

                tvExpired.visibility = if (((device.expiresAt?.toIntOrNull()) ?: 0) < 1) View.VISIBLE else View.GONE
            }
        }
    }

    fun String.capitalizeFirstLetter(): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = getItem(position)
        holder.bind(device)
    }

    internal class DeviceDiffCallback: DiffUtil.ItemCallback<DeviceEntity>(){
        override fun areItemsTheSame(oldItem: DeviceEntity, newItem: DeviceEntity): Boolean {
            return oldItem.deviceId == newItem.deviceId
        }

        override fun areContentsTheSame(oldItem: DeviceEntity, newItem: DeviceEntity): Boolean {
            return oldItem == newItem
        }
    }
}
