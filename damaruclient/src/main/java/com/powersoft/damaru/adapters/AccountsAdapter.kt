package com.powersoft.damaru.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.AccountEntity
import com.powersoft.damaru.databinding.ItemAccountBinding

class AccountsAdapter(
    private val isRootUser: Boolean,
    private val deviceList: List<AccountEntity>,
    private val clickListener: RecyclerViewItemClickListener<AccountEntity>,
) : RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAccountBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(account: AccountEntity) {
            binding.apply {
                binding.root.setOnClickListener {
                    clickListener.onItemClick(it.id, layoutPosition, account)
                }
                tvAccountName.text = account.accountName
                tvEmail.text = account.pin
                tvInitials.text = try {
                    account.accountName?.first()?.uppercase().toString()
                } catch (e: Exception) {
                    ""
                }
                holderAdminAccount.visibility = if (account.isAdmin == true) View.VISIBLE else View.GONE
                imgDelete.setOnClickListener {
                    clickListener.onItemClick(it.id, layoutPosition, account)
                }
                if (isRootUser && account.isAdmin == false) {
                    imgDelete.visibility = View.VISIBLE
                } else {
                    imgDelete.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = deviceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deviceList[position])
    }
}
