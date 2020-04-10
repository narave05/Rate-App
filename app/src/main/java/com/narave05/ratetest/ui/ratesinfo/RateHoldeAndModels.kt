package com.narave05.ratetest.ui.ratesinfo

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.narave05.ratetest.data.BankAndRateInfo
import com.narave05.ratetest.data.ExchangeType
import com.narave05.ratetest.ui.common.adapter.BaseViewHolder
import com.narave05.ratetest.ui.common.adapter.ItemClickListener
import com.narave05.ratetest.ui.common.adapter.ListItem
import kotlinx.android.synthetic.main.item_rate.view.*

const val RATE_LIST_ITEM_TYPE = 1

class RateHolder(itemView: View) :
    BaseViewHolder<RateListItem, ItemClickListener<RateListItem>>(itemView) {

    override fun bind(obj: RateListItem) {
        itemView.tag = obj
        itemView.bankNameTv.text = obj.bankName
        itemView.buyTv.text = obj.rateBuy.toString()
        itemView.sellTv.text = obj.rateSell.toString()
    }

    override fun initHolderListeners() {
        itemView.setOnClickListener { v ->
            if (adapterPosition != RecyclerView.NO_POSITION)
                listItemActionListener?.onAdapterItemClicked(v.tag as RateListItem, adapterPosition)
        }
    }
}

data class RateListItem(
    val bankId: String,
    val bankName: String,
    val rateBuy: Double,
    val rateSell: Double
) : ListItem {
    override val id: String get() = bankId
    override fun getType() = RATE_LIST_ITEM_TYPE
}

fun BankAndRateInfo.toRateListItem(exchangeType: ExchangeType): RateListItem? {
    val (bank,rate) = this
    return RateListItem(
        bankId = bank.id,
        bankName = bank.name,
        rateBuy = rate.getBuy(exchangeType),
        rateSell = rate.getSell(exchangeType)
    )
}