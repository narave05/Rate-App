package com.narave05.ratetest.ui.bankinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.narave05.ratetest.R
import com.narave05.ratetest.data.ExchangeType
import com.narave05.ratetest.ui.common.adapter.*
import com.narave05.ratetest.ui.common.livedata.EventObserver
import kotlinx.android.synthetic.main.bank_details_fragment.*
import kotlinx.android.synthetic.main.bank_details_fragment.rv
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class BankInfoFragment : Fragment(), ExchangeChangeAction, ItemClickListener<BranchListItem> {

    companion object {
        private const val BANK_ID_KEY = "bankIdKey"
        fun newInstance(bankId: String): Fragment {
            return BankInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(BANK_ID_KEY, bankId)
                }
            }
        }
    }

    private val viewModel by inject<BankInfoViewModel>{
        val id = arguments?.getString(BANK_ID_KEY) ?: ""
        parametersOf(id)
    }

    private val adapter by lazy {
        BaseViewAdapter<ListItem>(viewHolderFactory = BankInfoHoldersFactory(this))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bank_details_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv.adapter = adapter
        rv.addItemDecoration(DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL))
        viewModel.bankInfoLiveData.observe(this, Observer {
            adapter.newList(it)
        })
        viewModel.progressLiveData.observe(this, Observer {
            progressBar.visibility = if(it) View.VISIBLE else View.GONE
        })
        viewModel.errorLiveData.observe(this, EventObserver {
            Toast.makeText(requireContext(), R.string.error_text, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onExchangeChanged(exchangeType: ExchangeType) {
        Toast.makeText(requireContext(), "$exchangeType", Toast.LENGTH_SHORT).show()
        viewModel.onExchangeTypChanged(exchangeType)
    }

    override fun onAdapterItemClicked(item: BranchListItem, position: Int) {
        rv.smoothScrollToPosition(0)
        val bankListItem = adapter.getItem(0) as? BankListItem
        bankListItem?.apply {
            branchTitle = item.name
            address = item.address
            contacts = item.contacts
            workDays = item.workDays
        }
        adapter.notifyItemChanged(0)
    }
}
