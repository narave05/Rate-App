package com.narave05.ratetest.ui.common.adapter

import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T : ListItem, L : ListItemActionListener?>(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    protected val context: Context = itemView.context
    var listItemActionListener: L? = null

    @CallSuper
    open fun firstAttach() {
        initHolderListeners()
    }

    abstract fun bind(obj: T)

    open fun bind(obj: T, payload: MutableList<Any>) {
        bind(obj)
    }

    protected open fun initHolderListeners() {}

    open fun onViewDetached() {}
}
