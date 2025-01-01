package com.powersoft.common.listeners

interface RecyclerViewItemClickListener<T> {
    fun onItemClick(viewId : Int, position: Int, data: T)
}