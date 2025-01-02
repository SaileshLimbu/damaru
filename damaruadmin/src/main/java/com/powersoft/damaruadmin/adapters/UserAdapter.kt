package com.powersoft.damaruadmin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.UserEntity
import com.powersoft.damaruadmin.databinding.ItemUserBinding

class UserAdapter(
    private val deviceList: List<UserEntity>,
    private val listener: RecyclerViewItemClickListener<UserEntity>
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(it.id, layoutPosition, deviceList[layoutPosition])
            }
        }

        fun bind(user: UserEntity) {
            binding.apply {
                tvUsername.text = user.name
                tvUserEmailFuck.text = user.email
                tvTotalDevices.text = "${user.accounts?.size ?: 0}"
                tvTotalAccounts.text = "${user.accounts?.size ?: 0}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = deviceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deviceList[position])
    }
}