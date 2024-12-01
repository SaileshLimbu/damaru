package com.d1vivek.projectz.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.d1vivek.projectz.databinding.ItemDeviceUsersBinding

data class User(val name: String, val profileImage: Int)

class DeviceUserAdapter(private val users: List<User>, private val onClick: (User) -> Unit) : RecyclerView.Adapter<DeviceUserAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemDeviceUsersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userName.text = user.name
            binding.userImage.setImageResource(user.profileImage)
            binding.root.setOnClickListener { onClick(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeviceUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = users.size
}
