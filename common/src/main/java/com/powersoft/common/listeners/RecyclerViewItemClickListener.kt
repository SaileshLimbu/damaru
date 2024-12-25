package com.powersoft.common.listeners

interface RecyclerViewItemClickListener<T> {
    fun onItemClick(position: Int, data: T)
}