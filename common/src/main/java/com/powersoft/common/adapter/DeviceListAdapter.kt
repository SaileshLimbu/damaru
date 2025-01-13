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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.powersoft.common.R
import com.powersoft.common.databinding.ItemDeviceBinding
import com.powersoft.common.databinding.ItemDeviceListBinding
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.Status
import com.powersoft.common.utils.visibility

class DeviceListAdapter(
    private val clickListener: RecyclerViewItemClickListener<DeviceEntity>? = null,
    private val type: TYPE = TYPE.CONNECT,
    private val shouldShowStatus: Boolean = false
) : ListAdapter<DeviceEntity, DeviceListAdapter.ViewHolder>(DeviceDiffCallback()) {

    companion object {
        enum class TYPE {
            CONNECT, LIST
        }
    }

    private var originalList: List<DeviceEntity> = emptyList()

    fun submitOriginalList(list: List<DeviceEntity>) {
        originalList = list
        submitList(originalList)
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalList
        } else {
            // remaining days, Expired, registered email, created date
            originalList.filter {
                var withInExpire = false
                val expireAt = it.expiresAt?.toIntOrNull()
                val inputInt = query.toIntOrNull()
                if (expireAt != null && inputInt != null) {
                    if (expireAt <= inputInt) {
                        withInExpire = true
                    }
                }

                withInExpire
                        || (it.email?.contains(query, ignoreCase = true) ?: false)
                        || it.createdAt.contains(query, ignoreCase = true)
                        || (query.contains("expire", ignoreCase = true) && (it.expiresAt?.toIntOrNull() ?: 100) <= 0)
            }
        }
        submitList(filteredList)
    }

    fun removeItem(position: Int) {
        val currentList = currentList.toMutableList()
        currentList.removeAt(position)
        submitList(currentList)
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

                is ItemDeviceListBinding -> {
                    binding.root.setOnClickListener {
                        clickListener?.onItemClick(it.id, layoutPosition, getItem(layoutPosition))
                    }
                    binding.btnDelete.setOnClickListener {
                        clickListener?.onItemClick(it.id, layoutPosition, getItem(layoutPosition))
                    }
                    binding.btnExtend.setOnClickListener {
                        clickListener?.onItemClick(it.id, layoutPosition, getItem(layoutPosition))
                    }
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
                            ((device.expiresAt?.toIntOrNull()) ?: 0) < 1 -> ColorStateList.valueOf(colorRed)
                            else -> null
                        }

                        val colorStateList: ColorStateList = if (device.status == Status.online) {
                            ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.greenLight))
                        } else {
                            ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.primary_light))
                        }
                        imgStatus.imageTintList = colorStateList
                        tvStatus.text = device.status.name.capitalizeFirstLetter()

                        tvExpired.visibility = if (((device.expiresAt?.toIntOrNull()) ?: 0) < 1) View.VISIBLE else View.GONE
                    }
                }

                is ItemDeviceListBinding -> {
                    binding.apply {
                        tvDeviceName.text = device.deviceName
                        tvStatus.text = " [ ${device.state} ] "
                        tvStatus.visibility(shouldShowStatus)
                        btnDelete.visibility(!shouldShowStatus)
                        btnExtend.visibility(!shouldShowStatus)
                        lvlAssignedTo.visibility(shouldShowStatus && !(device.email.isNullOrEmpty()))
                        tvAssignedTo.visibility(shouldShowStatus && !(device.email.isNullOrEmpty()))
                        tvAssignedTo.text = device.email
                        Glide.with(itemView.context).load(device.screenshot)
                            .apply(RequestOptions.bitmapTransform(RoundedCorners(16)))
                            .into(imgDevice)
                        tvDeviceId.text = device.deviceId

                        if (device.status == Status.online) {
                            btnOnline.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.greenLight)))
                            btnOnline.strokeColor = (ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.greenLight)))
                            btnOnline.iconTint = (ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.greenLight)))
                            btnOnline.text = "Online"
                        } else {
                            btnOnline.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.red)))
                            btnOnline.strokeColor = (ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.red)))
                            btnOnline.iconTint = (ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.red)))
                            btnOnline.text = "Offline"
                        }

                        tvCreatedAt.text = "Created At\n${device.createdAt}"
                        tvUpdatedAt.text = "Updated At\n${device.updatedAt}"

                        if (((device.expiresAt?.toIntOrNull()) ?: 0) < 1 && !shouldShowStatus) {
                            holderExpired.visibility(true)
                            tvRemainingDays.visibility(false)
                        } else {
                            holderExpired.visibility(false)
                            if (device.expiresAt != null) {
                                tvRemainingDays.text = "Expires In : ${device.expiresAt} days"
                                tvRemainingDays.visibility(true)
                            } else {
                                tvRemainingDays.visibility(false)
                            }
                        }
                    }
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
