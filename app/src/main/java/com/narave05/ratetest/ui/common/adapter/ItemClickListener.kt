package com.narave05.ratetest.ui.common.adapter

interface ItemClickListener<T : ListItem> : ListItemActionListener {
    fun onAdapterItemClicked(item: T, position: Int = 0)
}