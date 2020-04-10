package com.narave05.ratetest.data

import com.narave05.ratetest.data.api.*

data class BankInfo(
    override val id: String,
    override val name: String,
    var branchInfos: List<BranchInfo> = listOf(),
    val rateInfos: List<RateInfo>
) : BankIdentification

interface BankIdentification {
    val id: String
    val name: String
}

data class BranchInfo(
    val isHead: Boolean,
    val id: String,
    val name: String,
    val address: String,
    val contacts: String,
    val workDays: String
)

data class RateInfo(
    val currencyType: CurrencyType,
    private val cash: Value,
    private val nonCash: Value
) {
    fun hasRateValue(type: ExchangeType) = getBuy(type) > 0 && getSell(type) > 0
    fun getBuy(type: ExchangeType) = if (type == ExchangeType.CASH) cash.buy else nonCash.buy
    fun getSell(type: ExchangeType) = if (type == ExchangeType.CASH) cash.sell else nonCash.sell
}

data class Value(
    val buy: Double,
    val sell: Double
)

fun Map<String, BankInfoRes>.toBanks() = map {
    it.value.toBank(it.key)
}.toList()

fun BankBranchesInfoRes.toBranches() = branchesMap.map {
    it.value.toBranch(it.key)
}.toList()


private fun BankInfoRes.toBank(id: String) = BankInfo(
    id = id,
    name = name,
    branchInfos = listOf(),
    rateInfos = ratesMap.toRates()
)

private fun Map<String, Map<String, RateRes>>.toRates() = map {
    it.value.toRate(it.key)
}.toList()

private fun Map<String, RateRes>.toRate(key: String) = RateInfo(
    currencyType = CurrencyType.values().find { it.key == key } ?: CurrencyType.UNKNOWN,
    cash = get(ExchangeType.CASH.key)?.toValue() ?: Value(buy = 0.0, sell = 0.0),
    nonCash = get(ExchangeType.NON_CASH.key)?.toValue() ?: Value(buy = 0.0, sell = 0.0)
)

private fun RateRes.toValue() = Value(buy = buy, sell = sell)

private fun BranchInfoRes.toBranch(id: String) = BranchInfo(
    isHead = head == 1,
    id = id,
    name = title[DEFAULT_LANG] ?: "",
    address = address[DEFAULT_LANG] ?: "",
    contacts = contacts,
    workDays = workHours.toWorkDaysString()
)

private fun List<WorkHourRes>.toWorkDaysString(): String {
    return buildString {
        this@toWorkDaysString.forEach{
            append("${it.days} ${it.hours} ")
        }
    }
}