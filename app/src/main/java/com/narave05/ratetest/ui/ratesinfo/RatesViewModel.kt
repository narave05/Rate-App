package com.narave05.ratetest.ui.ratesinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.narave05.ratetest.data.Action
import com.narave05.ratetest.data.BankAndStateInfoRepo
import com.narave05.ratetest.data.CurrencyType
import com.narave05.ratetest.data.ExchangeType
import com.narave05.ratetest.data.SortOrderType
import com.narave05.ratetest.ui.common.livedata.Event
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RatesViewModel(private val repo: BankAndStateInfoRepo) : ViewModel() {

    private val _rateListLiveData = MutableLiveData<List<RateListItem>>()
    val rateListLiveData: LiveData<List<RateListItem>> get() = _rateListLiveData
    private val _errorLiveData = MutableLiveData<Event<Unit>>()
    val errorLiveData: LiveData<Event<Unit>> get() = _errorLiveData
    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        _progressLiveData.postValue(false)
        _errorLiveData.postValue(Event(Unit))
    }

    init {
        needUpdateRates()
        observeRatesChanges()
    }

    private fun observeRatesChanges() {
        viewModelScope.launch(exceptionHandler) {
            repo.observeRatesChanges()
                .collect { list ->
                    val uiItems = list.mapNotNull {
                        it.toRateListItem(repo.getCurrentExchangeType())
                    }
                    _progressLiveData.postValue(false)
                    _rateListLiveData.postValue(uiItems)
                }
        }
    }

    private fun needUpdateRates() {
        _progressLiveData.postValue(true)
        viewModelScope.launch(exceptionHandler) {
            repo.updateRatesByAction()
        }
    }

    fun onSortTypeChanged(ordinal: Int) {
        viewModelScope.launch(exceptionHandler) {
            val sortOrderType = SortOrderType.values()[ordinal]
            repo.updateRatesByAction(Action.SortOrderChange(sortOrderType))
        }
    }

    fun onCurrencyTypeChanged(ordinal: Int) {
        viewModelScope.launch(exceptionHandler) {
            val currencyType = CurrencyType.values()[ordinal]
            repo.updateRatesByAction(Action.CurrencyTypeChange(currencyType))
        }
    }

    fun onExchangeTypChanged(ordinal: Int) {
        viewModelScope.launch(exceptionHandler) {
            val exchangeType = ExchangeType.values()[ordinal]
            repo.updateRatesByAction(Action.ExchangeTypeChange(exchangeType))
        }
    }
}

