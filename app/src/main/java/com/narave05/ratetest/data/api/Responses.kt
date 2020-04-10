package com.narave05.ratetest.data.api

import com.google.gson.annotations.SerializedName

data class BranchInfoRes(
    @SerializedName("address")
    val address: Map<String, String>,
    @SerializedName("contacts")
    val contacts: String,
    @SerializedName("head")
    val head: Int,
    @SerializedName("title")
    val title: Map<String, String>,
    @SerializedName("workhours")
    val workHours: List<WorkHourRes>
)

data class WorkHourRes(
    @SerializedName("days")
    val days: String,
    @SerializedName("hours")
    val hours: String
)

data class BankBranchesInfoRes(
    @SerializedName("list")
    val branchesMap : Map<String, BranchInfoRes>
)


data class BankInfoRes(
    @SerializedName("title")
    val name : String,
    @SerializedName("list")
    val ratesMap : Map<String, Map<String, RateRes>>
)

data class RateRes(
    @SerializedName("buy")
    val buy : Double,
    @SerializedName("sell")
    val sell : Double
)