package com.powersoft.common.adapter

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
import com.powersoft.common.R
import com.powersoft.common.databinding.ItemDeviceBinding
import com.powersoft.common.databinding.ItemDeviceListBinding
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.Status

class DeviceListAdapter(
    private val clickListener: RecyclerViewItemClickListener<DeviceEntity>? = null,
    private val type: TYPE = TYPE.CONNECT
) : ListAdapter<DeviceEntity, DeviceListAdapter.ViewHolder>(DeviceDiffCallback()) {

    companion object {
        enum class TYPE {
            CONNECT, LIST
        }
    }

    inner class ViewHolder(private val binding: Any) :
        RecyclerView.ViewHolder(
            when (binding) {
                is ItemDeviceBinding -> binding.root
                is ItemDeviceListBinding -> binding.root
                else -> throw IllegalArgumentException("not supported binding")
            }
        ) {

        init {
            when (binding) {
                is ItemDeviceBinding -> binding.root.setOnClickListener {
                    clickListener?.onItemClick(it.id, layoutPosition, getItem(layoutPosition))
                }

                is ItemDeviceListBinding -> binding.root.setOnClickListener {
                    clickListener?.onItemClick(it.id, layoutPosition, getItem(layoutPosition))
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(device: DeviceEntity) {
            when (binding) {
                is ItemDeviceBinding -> {
                    binding.apply {
                        tvDeviceName.text = device.deviceName
                        Glide.with(itemView.context).load(device.screenshot).into(imgDevice)
                        tvRemainingDays.text = "${device.expiresAt} days"

                        val colorRed = ContextCompat.getColor(itemView.context, R.color.red)
                        tvRemainingDays.backgroundTintList = when {
                            ((device.expiresAt.toIntOrNull()) ?: 0) < 1 -> ColorStateList.valueOf(colorRed)
                            else -> null
                        }

                        val colorStateList: ColorStateList = if (device.status == Status.online) {
                            ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.greenLight))
                        } else {
                            ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.primary_light))
                        }
                        imgStatus.imageTintList = colorStateList
                        tvStatus.text = device.status.name.capitalizeFirstLetter()

                        tvExpired.visibility = if (((device.expiresAt.toIntOrNull()) ?: 0) < 1) View.VISIBLE else View.GONE
                    }
                }

                is ItemDeviceListBinding -> {
                    binding.apply { }
                }
            }
        }
    }

    fun String.capitalizeFirstLetter(): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = if (type == TYPE.CONNECT) ItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        else ItemDeviceListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = getItem(position)
        holder.bind(device)
    }

    internal class DeviceDiffCallback : DiffUtil.ItemCallback<DeviceEntity>() {
        override fun areItemsTheSame(oldItem: DeviceEntity, newItem: DeviceEntity): Boolean {
            return oldItem.deviceId == newItem.deviceId
        }

        override fun areContentsTheSame(oldItem: DeviceEntity, newItem: DeviceEntity): Boolean {
            return oldItem == newItem
        }
    }
}
