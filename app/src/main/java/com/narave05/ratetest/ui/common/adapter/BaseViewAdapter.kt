package com.narave05.ratetest.ui.common.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class BaseViewAdapter<T : ListItem>(
    private val listItems: MutableList<T> = mutableListOf(),
    private val viewHolderFactory: ViewHolderFactory
) : RecyclerView.Adapter<BaseViewHolder<in ListItem, in ListItemActionListener>>() {


    private var deleteListener: ItemDeleteListener? = null
    fun setDeleteListener(deleteListener: ItemDeleteListener) {
        this.deleteListener = deleteListener
    }

    private var itemClickListener: ItemClickListener<T>? = null
    fun setUniversalClickListener(itemClickListener: ItemClickListener<T>) {
        this.itemClickListener = itemClickListener
    }

    val items: List<T>
        get() = listItems.toList()

    val isEmpty: Boolean
        get() = listItems.isEmpty()


    override fun getItemViewType(position: Int): Int {
        return listItems[position].getType()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<in ListItem, in ListItemActionListener> {
        val holder = viewHolderFactory.create(parent, viewType)
        holder.itemView.setOnClickListener {
            val adapterPosition = holder.getAdapterPosition()
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val item = getItem(adapterPosition) ?: return@setOnClickListener
                itemClickListener?.onAdapterItemClicked(item, adapterPosition)
            }
        }
        holder.firstAttach()
        return holder
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<in ListItem, in ListItemActionListener>,
        position: Int
    ) {
        val obj = listItems[position]
        holder.bind(obj)
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<in ListItem, in ListItemActionListener>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val obj = listItems[position]
            holder.bind(obj, payloads)
        }
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<in ListItem, in ListItemActionListener>) {
        holder.onViewDetached()
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    fun getItem(position: Int): T? {
        return if (position < 0 || position > listItems.size - 1) {
            null
        } else listItems[position]
    }

    fun deleteItemAtIndex(position: Int) {
        if (position < 0 || position >= listItems.size) return
        val obj = listItems.removeAt(position)
        notifyItemRemoved(position)
        onItemDeleted(obj, position)
    }

    fun deleteItem(obj: T) {
        val position = listItems.indexOf(obj)
        if (position == -1) return
        listItems.removeAt(position)
        notifyItemRemoved(position)
        onItemDeleted(obj, position)
    }

    fun newList(newList: List<T>) {
        val diffCallback = DiffCallback(listItems, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listItems.clear()
        listItems.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addItem(obj: T): Boolean {
        listItems.add(obj)
        notifyItemInserted(listItems.size - 1)
        return true
    }

    fun addItem(obj: T, position: Int): Boolean {
        if (position < 0 || position >= listItems.size) return false
        listItems.add(position, obj)
        notifyItemInserted(position)
        return true
    }

    fun updateItem(obj: T): Boolean {
        val itemPosition = listItems.indexOf(obj)
        return if (itemPosition != -1) {
            listItems[itemPosition] = obj
            notifyItemChanged(itemPosition, true)
            true
        } else {
            false
        }
    }

    fun updateItem(obj: T, position: Int): Boolean {
        if (position < 0 || position >= listItems.size) return false
        listItems[position] = obj
        //notify item changed without animation :)
        notifyItemChanged(position, true)
        return true
    }

    fun addNewList(newList: List<T>) {
        val start = listItems.size
        listItems.addAll(newList)
        notifyItemRangeInserted(start, newList.size)
    }

    private fun onItemDeleted(obj: T, position: Int) {
        deleteListener?.onItemDeleted(obj, position)
    }

}

class DiffCallback(private val oldList: List<ListItem>, private val newList: List<ListItem>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id === newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int) =
        oldList[oldPosition] == newList[newPosition]
}
