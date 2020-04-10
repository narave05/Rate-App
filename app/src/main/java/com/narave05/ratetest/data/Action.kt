package com.narave05.ratetest.data

sealed class Action {
    object GetRates : Action()
    data class GetBranches(val bankId: String) : Action()
    data class SortOrderChange(val type: SortOrderType) : Action()
    data class ExchangeTypeChange(val type: ExchangeType) : Action()
    data class CurrencyTypeChange(val type: CurrencyType) : Action()
}