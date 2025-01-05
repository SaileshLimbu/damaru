package com.powersoft.damaru.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.utils.visibility
import com.powersoft.damaru.databinding.ItemAccountBinding

class AccountsAdapter(
    private val adapterFor : For,
    private val isRootUser: Boolean,
    private val clickListener: RecyclerViewItemClickListener<AccountEntity>,
) : ListAdapter<AccountEntity, AccountsAdapter.ViewHolder>(AccountDiffCallback()) {

    companion object{
        enum class For{
            LINKED_ACCOUNTS, ACCOUNT_LIST
        }
    }

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
                holderAdminAccount.visibility(account.isAdmin)
                imgDelete.setOnClickListener {
                    clickListener.onItemClick(it.id, layoutPosition, account)
                }
                imgEdit.setOnClickListener {
                    clickListener.onItemClick(it.id, layoutPosition, account)
                }
                imgDelete.visibility(isRootUser && !account.isAdmin)
                imgEdit.visibility((isRootUser || account.isAdmin) && adapterFor != For.LINKED_ACCOUNTS)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    internal class AccountDiffCallback: DiffUtil.ItemCallback<AccountEntity>(){
        override fun areItemsTheSame(oldItem: AccountEntity, newItem: AccountEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AccountEntity, newItem: AccountEntity): Boolean {
            return oldItem == newItem
        }
    }
}
