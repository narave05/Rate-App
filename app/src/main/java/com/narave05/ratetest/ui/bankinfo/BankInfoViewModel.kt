package com.narave05.ratetest.ui.bankinfo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.narave05.ratetest.data.Action
import com.narave05.ratetest.data.BankAndStateInfoRepo
import com.narave05.ratetest.data.BankInfo
import com.narave05.ratetest.data.ExchangeType
import com.narave05.ratetest.ui.common.adapter.ListItem
import com.narave05.ratetest.ui.common.livedata.Event
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BankInfoViewModel(
    private val bankId: String,
    private val repo: BankAndStateInfoRepo
) : ViewModel() {

    private val _bankInfoLiveData = MutableLiveData<List<ListItem>>()
    val bankInfoLiveData: LiveData<List<ListItem>> get() = _bankInfoLiveData
    private val _errorLiveData = MutableLiveData<Event<Unit>>()
    val errorLiveData: LiveData<Event<Unit>> get() = _errorLiveData
    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        _progressLiveData.postValue(false)
        _errorLiveData.postValue(Event(Unit))
    }

    init {
        observeBankInfoChanges()
        needUpdateBankInfoBy()
    }

    private fun observeBankInfoChanges() {
        viewModelScope.launch(exceptionHandler) {
            repo.observeBankDetailChanges(bankId)
                .collect {
                    if (it.branchInfos.isEmpty()) return@collect
                    Log.e("funf", "${it.rateInfos}")
                    _progressLiveData.postValue(false)
                    val uiList = buildUiList(it)
                    _bankInfoLiveData.postValue(uiList)
                }
        }
    }

    private suspend fun buildUiList(it: BankInfo): List<ListItem> {
        val uiList = mutableListOf<ListItem>()
        uiList.add(it.toBankListItem())
        val exchangeType = repo.getCurrentExchangeType()
        uiList.add(ExchangeListItem(exchangeType))
        if (it.rateInfos.isNotEmpty()) {
            uiList.add(HeaderListItem("Currency"))
        }
        it.rateInfos.forEach { rate ->
           rate.toCurrencyListItem(exchangeType)?.let {
               uiList.add(it)
           }
        }
        uiList.add(HeaderListItem("All branches"))
        it.branchInfos.forEach { branch ->
            uiList.add(branch.toBranchListItem())
        }
        return uiList
    }

    private fun needUpdateBankInfoBy() {
        viewModelScope.launch(exceptionHandler) {
            repo.updateBankDetailAction(Action.GetBranches(bankId))
        }
    }

    fun onExchangeTypChanged(exchangeType: ExchangeType) {
        viewModelScope.launch(exceptionHandler) {
            repo.updateRatesByAction(Action.ExchangeTypeChange(exchangeType))
        }
    }
}
