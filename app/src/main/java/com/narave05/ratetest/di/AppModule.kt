package com.narave05.ratetest.di

import com.narave05.ratetest.data.api.BanksAndRatesInfoService
import com.narave05.ratetest.data.BankAndStateInfoRepo
import com.narave05.ratetest.data.Storage
import com.narave05.ratetest.data.api.common.ApiServiceConfig
import com.narave05.ratetest.data.api.common.ApiServiceCreator
import com.narave05.ratetest.ui.bankinfo.BankInfoViewModel
import com.narave05.ratetest.ui.ratesinfo.RatesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

 fun appModule( baseUrl: String, isDebugMode: Boolean) = module {

    single {
        val config = ApiServiceConfig(
            baseUrl = baseUrl,
            isDebugMode = isDebugMode
        )
        ApiServiceCreator(config)
    }

    single {
        get<ApiServiceCreator>().create<BanksAndRatesInfoService>()
    }

    single {
        BankAndStateInfoRepo(service = get(), storage = get())
    }

     single {
         Storage()
     }

     viewModel {
         RatesViewModel(repo = get())
     }

     viewModel {(id:String)->
         BankInfoViewModel(bankId = id, repo = get())
     }
}