package com.narave05.ratetest.ui.bankinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.narave05.ratetest.R
import com.narave05.ratetest.data.BankInfo
import com.narave05.ratetest.data.BranchInfo
import com.narave05.ratetest.data.ExchangeType
import com.narave05.ratetest.data.RateInfo
import com.narave05.ratetest.ui.common.adapter.*
import kotlinx.android.synthetic.main.item_bank.view.*
import kotlinx.android.synthetic.main.item_branch.view.*
import kotlinx.android.synthetic.main.item_currency.view.*
import kotlinx.android.synthetic.main.item_exchange.view.*
import kotlinx.android.synthetic.main.item_header.view.*
import kotlinx.android.synthetic.main.item_rate.view.buyTv
import kotlinx.android.synthetic.main.item_rate.view.sellTv

const val BANK_LIST_ITEM_TYPE = 2
const val EXCHANGE_LIST_ITEM_TYPE = 3
const val CURRENCY_LIST_ITEM_TYPE = 4
const val HEADER_LIST_ITEM_TYPE = 6
const val BRANCH_LIST_ITEM_TYPE = 7

class BankInfoHoldersFactory(listener: ListItemActionListener? = null) :
    ViewHolderFactory(listener) {
    override fun create(
        parent: ViewGroup,
        layoutInflater: LayoutInflater,
        viewType: Int
    ): BaseViewHolder<*, *> {
        return when (viewType) {
            BANK_LIST_ITEM_TYPE -> {
                val holderView = layoutInflater.inflate(R.layout.item_bank, parent, false)
                BankHolder(holderView)
            }
            EXCHANGE_LIST_ITEM_TYPE -> {
                val holderView = layoutInflater.inflate(R.layout.item_exchange, parent, false)
                ExchangeHolder(holderView)
            }
            CURRENCY_LIST_ITEM_TYPE -> {
                val holderView = layoutInflater.inflate(R.layout.item_currency, parent, false)
                CurrencyHolder(holderView)
            }
            HEADER_LIST_ITEM_TYPE -> {
                val holderView = layoutInflater.inflate(R.layout.item_header, parent, false)
                HeaderHolder(holderView)
            }
            BRANCH_LIST_ITEM_TYPE -> {
                val holderView = layoutInflater.inflate(R.layout.item_branch, parent, false)
                BranchHolder(holderView)
            }
            else -> throw IllegalArgumentException("not support type")
        }
    }
}

class BankHolder(itemView: View) :
    BaseViewHolder<BankListItem, ListItemActionListener>(itemView) {

    override fun bind(obj: BankListItem) {
        itemView.tag = obj
        itemView.nameTv.text = obj.bankName
        itemView.branchTitleTv.text = obj.branchTitle
        itemView.addressTv.text = obj.address
        itemView.contactTv.text = "Tel: ${obj.contacts}"
        itemView.workingDayTv.text = "Working days: ${obj.workDays}"
    }
}

data class BankListItem(
    val bankName: String,
    var branchTitle: String = "",
    var address: String = "",
    var contacts: String = "",
    var workDays: String = ""
) : ListItem {
    override val id: String get() = "BankHeadItem"
    override fun getType() = BANK_LIST_ITEM_TYPE
}

interface ExchangeChangeAction : ListItemActionListener {
    fun onExchangeChanged(exchangeType: ExchangeType)
}

class ExchangeHolder(itemView: View) :
    BaseViewHolder<ExchangeListItem, ExchangeChangeAction>(itemView) {

    override fun bind(obj: ExchangeListItem) {
        itemView.tag = obj
        when {
            obj.type == ExchangeType.CASH && !itemView.cashRb.isChecked -> itemView.rg.check(R.id.cashRb)
            obj.type == ExchangeType.NON_CASH && !itemView.nonCashRb.isChecked -> itemView.rg.check(R.id.nonCashRb)
        }
    }

    override fun bind(obj: ExchangeListItem, payload: MutableList<Any>) {
        //skip radio baton twice choice
    }

    override fun initHolderListeners() {
        itemView.rg.setOnCheckedChangeListener() { _, i ->
            val type = if (i == R.id.cashRb) ExchangeType.CASH else ExchangeType.NON_CASH
            listItemActionListener?.onExchangeChanged(type)
        }
    }
}

data class ExchangeListItem(
    val type: ExchangeType
) : ListItem, Payloadable {
    override val id: String get() = "ExchangeItem"
    override val payload: Any get() = type
    override fun getType() = EXCHANGE_LIST_ITEM_TYPE
}

class CurrencyHolder(itemView: View) :
    BaseViewHolder<CurrencyListItem, ListItemActionListener?>(itemView) {

    override fun bind(obj: CurrencyListItem) {
        itemView.tag = obj
        itemView.currencyTv.text = obj.currency
        itemView.buyTv.text = obj.rateBuy.toString()
        itemView.sellTv.text = obj.rateSell.toString()
    }
}

data class CurrencyListItem(
    val currency: String,
    val rateBuy: Double,
    val rateSell: Double
) : ListItem {
    override val id: String get() = currency
    override fun getType() = CURRENCY_LIST_ITEM_TYPE
}

class HeaderHolder(itemView: View) :
    BaseViewHolder<HeaderListItem, ListItemActionListener?>(itemView) {

    override fun bind(obj: HeaderListItem) {
        itemView.tag = obj
        itemView.headerTv.text = obj.text
    }
}

data class HeaderListItem(
    val text: String
) : ListItem {
    override val id: String get() = text
    override fun getType() = HEADER_LIST_ITEM_TYPE
}

class BranchHolder(itemView: View) :
    BaseViewHolder<BranchListItem, ItemClickListener<BranchListItem>>(itemView) {

    override fun bind(obj: BranchListItem) {
        itemView.tag = obj
        itemView.branchNameTv.text = obj.name
    }

    override fun initHolderListeners() {
        itemView.setOnClickListener { v ->
            if (adapterPosition != RecyclerView.NO_POSITION)
                listItemActionListener?.onAdapterItemClicked(
                    v.tag as BranchListItem,
                    adapterPosition
                )
        }
    }
}

data class BranchListItem(
    override val id: String,
    val name: String,
    val address: String,
    val contacts: String,
    val workDays: String
) : ListItem {
    override fun getType() = BRANCH_LIST_ITEM_TYPE
}

fun RateInfo.toCurrencyListItem(exchangeType: ExchangeType): CurrencyListItem? {
    if (!hasRateValue(exchangeType)) return null
    return CurrencyListItem(
        currency = currencyType.key,
        rateBuy = getBuy(exchangeType),
        rateSell = getSell(exchangeType)
    )
}

fun BankInfo.toBankListItem(): BankListItem {
    val branchInfo = branchInfos.find { branch ->
        branch.isHead
    } ?: branchInfos[0]
    return BankListItem(
        bankName = name,
        branchTitle = branchInfo.name,
        address = branchInfo.address,
        contacts = branchInfo.contacts,
        workDays = branchInfo.workDays
    )
}

fun BranchInfo.toBranchListItem(): BranchListItem {
    return BranchListItem(
        id = id,
        name = name,
        address = address,
        contacts = contacts,
        workDays = workDays
    )
}

