package com.narave05.ratetest.ui.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

abstract class ViewHolderFactory(private val listItemActionListener: ListItemActionListener? = null) {

    @Suppress("UNCHECKED_CAST")
    fun create(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<in ListItem, in ListItemActionListener> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val baseViewHolder = create(
            parent,
            layoutInflater,
            viewType
        ) as BaseViewHolder<in ListItem, in ListItemActionListener>
        baseViewHolder.listItemActionListener = listItemActionListener
        return baseViewHolder
    }

    abstract fun create(
        parent: ViewGroup,
        layoutInflater: LayoutInflater,
        viewType: Int
    ): BaseViewHolder<*, *>
}
