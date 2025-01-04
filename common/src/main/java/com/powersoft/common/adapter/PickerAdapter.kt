package com.powersoft.common.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.powersoft.common.model.PickerEntity

class PickerAdapter(
    private val items: List<PickerEntity>, private val isMultiSelect: Boolean, private val onItemSelected: (List<PickerEntity>) -> Unit
) : RecyclerView.Adapter<PickerAdapter.ItemViewHolder>() {

    val selectedItems = mutableSetOf<PickerEntity>()

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)

        init {
            textView.setPadding(120, 64, 120, 64)
            itemView.setOnClickListener {
                val item = items[adapterPosition]
                if (isMultiSelect) {
                    if (selectedItems.contains(item)) {
                        selectedItems.remove(item)
                    } else {
                        selectedItems.add(item)
                    }
                } else {
                    selectedItems.clear()
                    selectedItems.add(item)
                }
                notifyDataSetChanged()
                onItemSelected(selectedItems.toList())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.value.toString()
        holder.itemView.setBackgroundColor(
            if (selectedItems.contains(item)) Color.LTGRAY else Color.WHITE
        )
    }

    override fun getItemCount(): Int = items.size
}
