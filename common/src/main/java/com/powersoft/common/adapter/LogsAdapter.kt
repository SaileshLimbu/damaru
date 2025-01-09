package com.powersoft.common.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.powersoft.common.databinding.ItemLogsBinding
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.LogsEntity

class LogsAdapter(
    private val listener: RecyclerViewItemClickListener<LogsEntity>
) : ListAdapter<LogsEntity, LogsAdapter.ViewHolder>(UserDiffCallback()) {

    inner class ViewHolder(private val binding: ItemLogsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(it.id, layoutPosition, getItem(layoutPosition))
            }
        }

        fun bind(log: LogsEntity) {
            binding.apply {
                tvLog.text = Html.fromHtml(log.desc, Html.FROM_HTML_MODE_COMPACT)
                tvDate.text = log.date
            }
        }
    }

    fun removeItem(position: Int) {
        val currentList = currentList.toMutableList()
        currentList.removeAt(position)
        submitList(currentList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLogsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    internal class UserDiffCallback : DiffUtil.ItemCallback<LogsEntity>() {
        override fun areItemsTheSame(oldItem: LogsEntity, newItem: LogsEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LogsEntity, newItem: LogsEntity): Boolean {
            return oldItem == newItem
        }
    }
}