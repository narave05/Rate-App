package com.narave05.ratetest.ui.ratesinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.narave05.ratetest.R
import com.narave05.ratetest.ui.bankinfo.BankInfoFragment
import com.narave05.ratetest.ui.common.adapter.BaseViewAdapter
import com.narave05.ratetest.ui.common.adapter.BaseViewHolder
import com.narave05.ratetest.ui.common.adapter.ItemClickListener
import com.narave05.ratetest.ui.common.adapter.ViewHolderFactory
import com.narave05.ratetest.ui.common.livedata.EventObserver
import kotlinx.android.synthetic.main.rates_fragment.*
import org.koin.android.ext.android.inject

class RatesFragment : Fragment(), ItemClickListener<RateListItem> {

    private val viewModel by inject<RatesViewModel>()

    private val adapter by lazy {
        BaseViewAdapter<RateListItem>(viewHolderFactory = object : ViewHolderFactory(this) {
            override fun create(
                parent: ViewGroup,
                layoutInflater: LayoutInflater,
                viewType: Int
            ): BaseViewHolder<*, *> {
                val holderView = layoutInflater.inflate(R.layout.item_rate, parent, false)
                return RateHolder(holderView)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.rates_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv.adapter = adapter
        sortOrderBt.setOnClickListener {
            showSortDialog()
        }
        currencyBt.setOnClickListener {
            showCurrenciesDialog()
        }
        exchangeBt.setOnClickListener {
            showExchangeTypeDialog()
        }
        viewModel.rateListLiveData.observe(this, Observer {
            adapter.newList(it)
        })

        viewModel.progressLiveData.observe(this, Observer {
            progressBar.visibility = if(it) View.VISIBLE else View.GONE
        })
        viewModel.errorLiveData.observe(this, EventObserver {
            Toast.makeText(requireContext(), R.string.error_text, Toast.LENGTH_SHORT).show()
        })
    }

    private fun showSortDialog() {
        configAndShowDialog(R.string.sort_dialog_title, R.array.sort_orders) {
            viewModel.onSortTypeChanged(it)
        }
    }

    private fun showCurrenciesDialog() {
        configAndShowDialog(R.string.currencies_dialog_title, R.array.currencies) {
            viewModel.onCurrencyTypeChanged(it)
        }
    }

    private fun showExchangeTypeDialog() {
        configAndShowDialog(R.string.exchange_dialog_title, R.array.exchange_type) {
            viewModel.onExchangeTypChanged(it)
        }
    }

    private fun configAndShowDialog(
        @StringRes titleId: Int,
        @ArrayRes itemTextListId: Int,
        block: (Int) -> Unit
    ) {
        AlertDialog.Builder(requireContext()).run {
            setTitle(titleId)
            setItems(itemTextListId) { dialog, which ->
                block(which)
                dialog.dismiss()
            }
            create()
        }.show()
    }

    override fun onAdapterItemClicked(item: RateListItem, position: Int) {
        //TODO move to navigation component :)
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, BankInfoFragment.newInstance(item.bankId))
            addToBackStack(null)
            commit()
        }
    }
}
