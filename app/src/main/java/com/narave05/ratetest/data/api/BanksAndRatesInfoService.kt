package com.narave05.ratetest.data.api

import retrofit2.http.GET
import retrofit2.http.Query
const val DEFAULT_LANG = "en"
interface BanksAndRatesInfoService {

    @GET("rates.ashx")
    suspend fun getBanksAndRates(@Query("lang") lang: String = DEFAULT_LANG): Map<String, BankInfoRes>

    @GET("branches.ashx")
    suspend fun getBankDetails(@Query("id") id: String): BankBranchesInfoRes
}
