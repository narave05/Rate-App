package com.narave05.ratetest.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

private val DoNothing = {}
typealias BankAndRateInfo = Pair<BankIdentification,RateInfo>

@OptIn(FlowPreview::class)
class Storage {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val broadcastChannel = ConflatedBroadcastChannel<List<BankInfo>>()

    private var sortOrderState = SortOrderType.UNSORTED
    private var currencyState = CurrencyType.USD
    var exchangeTypeState = ExchangeType.CASH
        private set

    val banksListIsReady get() = banks.isNotEmpty()
    var banks = listOf<BankInfo>()
    val branchesMap = mutableMapOf<String, List<BranchInfo>>()


    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun observeRatesChanges(): Flow<List<BankAndRateInfo>> {
        return broadcastChannel.asFlow()
            .map {
                it.mapNotNull(::getRateAndBankOrNull)
                    .filter { (_,rate) -> rate.hasRateValue(exchangeTypeState) }
            }
            .map(::sort)
            .distinctUntilChanged()
    }

    private suspend fun sort(list: List<BankAndRateInfo>): List<BankAndRateInfo> {
        return when (sortOrderState) {
            SortOrderType.UNSORTED -> list
            SortOrderType.BUY_ASCENDING -> list.sortedBy { (_,rate) -> rate.getBuy(exchangeTypeState) }
            SortOrderType.BUY_DESCENDING -> list.sortedByDescending { (_,rate) -> rate.getBuy(exchangeTypeState) }
            SortOrderType.SELL_ASCENDING -> list.sortedBy { (_,rate) -> rate.getSell(exchangeTypeState) }
            SortOrderType.SELL_DESCENDING -> list.sortedByDescending { (_,rate) -> rate.getSell(exchangeTypeState) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun notifyFromAction(action: Action) {
        when (action) {
            is Action.SortOrderChange -> sortOrderState = action.type
            is Action.ExchangeTypeChange -> exchangeTypeState = action.type
            is Action.CurrencyTypeChange -> currencyState = action.type
            else -> DoNothing
        }
        broadcastChannel.send(banks)
    }


    private fun getRateAndBankOrNull(bankInfo: BankInfo): BankAndRateInfo? {
        return bankInfo.rateInfos.find {
            it.currencyType == currencyState
        }?.let {
            Pair(bankInfo,it)
        }
    }

    suspend fun observeBankDetailChanges(bankId: String): Flow<BankInfo> {
        return broadcastChannel.asFlow()
            .map { list ->
                list.find { it.id == bankId }
            }
            .map { bank ->
                bank?.apply {
                    branchesMap[bankId]?.let {
                        branchInfos = it
                    }
                }
            }
            .filterNotNull()
    }
}

