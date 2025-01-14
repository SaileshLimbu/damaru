package com.powersoft.damaruadmin.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.UserEntity
import com.powersoft.damaruadmin.databinding.ItemUserBinding

class UserAdapter(
    private val listener: RecyclerViewItemClickListener<UserEntity>
) : ListAdapter<UserEntity, UserAdapter.ViewHolder>(UserDiffCallback()) {

    private var originalList: List<UserEntity> = emptyList()

    fun submitOriginalList(list: List<UserEntity>) {
        originalList = list
        submitList(originalList)
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter { it.name.contains(query, ignoreCase = true) || it.email.contains(query, ignoreCase = true) }
        }
        submitList(filteredList)
    }

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
                tvUserEmail.text = user.email
                tvPin.text = user.pin
                tvTotalDevices.text = "${user.emulatorCount}"
                tvTotalAccounts.text = "${user.accountsCount}"

                btnEdit.setOnClickListener {
                    listener.onItemClick(it.id, layoutPosition, user)
                }
                btnDelete.setOnClickListener {
                    listener.onItemClick(it.id, layoutPosition, user)
                }
                btnAssign.setOnClickListener {
                    listener.onItemClick(it.id, layoutPosition, user)
                }

                btnDelete.isEnabled = !user.isSuperAdmin
                btnEdit.isEnabled = !user.isSuperAdmin
                btnAssign.isEnabled = !user.isSuperAdmin
                if (user.isSuperAdmin) {
                    btnDelete.setTextColor(ColorStateList.valueOf(Color.GRAY))
                    btnDelete.iconTint = ColorStateList.valueOf(Color.GRAY)
                    btnEdit.setTextColor(ColorStateList.valueOf(Color.GRAY))
                    btnEdit.iconTint = ColorStateList.valueOf(Color.GRAY)
                    btnAssign.setTextColor(ColorStateList.valueOf(Color.GRAY))
                    btnAssign.iconTint = ColorStateList.valueOf(Color.GRAY)
                } else {
                    btnDelete.setTextColor(ColorStateList.valueOf(Color.BLACK))
                    btnDelete.iconTint = ColorStateList.valueOf(Color.BLACK)
                    btnEdit.setTextColor(ColorStateList.valueOf(Color.BLACK))
                    btnEdit.iconTint = ColorStateList.valueOf(Color.BLACK)
                    btnAssign.setTextColor(ColorStateList.valueOf(Color.BLACK))
                    btnAssign.iconTint = ColorStateList.valueOf(Color.BLACK)
                }
            }
        }
    }

    fun removeItem(position: Int) {
        val currentList = currentList.toMutableList()
        currentList.removeAt(position)
        submitList(currentList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    internal class UserDiffCallback : DiffUtil.ItemCallback<UserEntity>() {
        override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
            return oldItem == newItem
        }
    }
}