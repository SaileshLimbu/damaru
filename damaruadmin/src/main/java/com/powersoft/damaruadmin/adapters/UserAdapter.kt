package com.powersoft.damaruadmin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.UserEntity
import com.powersoft.damaruadmin.databinding.ItemUserBinding

class UserAdapter(
    private val listener: RecyclerViewItemClickListener<UserEntity>
) : ListAdapter<UserEntity, UserAdapter.ViewHolder>(UserDiffCallback()) {

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(it.id, layoutPosition, getItem(layoutPosition))
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    internal class UserDiffCallback: DiffUtil.ItemCallback<UserEntity>(){
        override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
            return oldItem == newItem
        }
    }
}