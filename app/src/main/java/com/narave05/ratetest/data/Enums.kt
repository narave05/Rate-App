package com.narave05.ratetest.data

enum class CurrencyType(val key: String) {
    USD("USD"),
    EUR("EUR"),
    RUR("RUR"),
    AUD("AUD"),
    CAD("CAD"),
    CHF("CHF"),
    GBP("GBP"),
    GEL("GEL"),
    JPY("JPY"),
    XAU("XAU"),
    UNKNOWN("UNKNOWN")
}

enum class ExchangeType(val key: String) {
    CASH("1"),
    NON_CASH("0")
}

enum class WeekDay(val key: Int) {
    MON(1),
    TUE(2),
    WED(3),
    THU(4),
    FRI(5),
    SAT(6),
    SUN(7),
}

enum class SortOrderType{
    BUY_ASCENDING,
    BUY_DESCENDING,
    SELL_ASCENDING,
    SELL_DESCENDING,
    UNSORTED
}