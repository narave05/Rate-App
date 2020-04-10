package com.narave05.ratetest.ui.common.adapter

interface ListItem {
    val id: String
    fun getType(): Int
}

interface Payloadable {
    val payload: Any
}
