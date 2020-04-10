package com.narave05.ratetest.data

import com.narave05.ratetest.data.api.BanksAndRatesInfoService

class BankAndStateInfoRepo(
    private val service: BanksAndRatesInfoService,
    private val storage: Storage
) {

    suspend fun updateRatesByAction(action: Action = Action.GetRates) {
        banksInfoIsReady {
            storage.notifyFromAction(action)
        }
    }

    suspend fun updateBankDetailAction(action: Action.GetBranches) {
        banksInfoIsReady {
            branchInfoIsReady(action.bankId) {
                storage.notifyFromAction(action)
            }
        }
    }

    suspend fun getCurrentExchangeType() = storage.exchangeTypeState

    suspend fun observeRatesChanges() = storage.observeRatesChanges()

    suspend fun observeBankDetailChanges(bankId: String) = storage.observeBankDetailChanges(bankId)

    private suspend inline fun banksInfoIsReady(block: () -> Unit) {
        if (storage.banksListIsReady) {
            block()
        } else {
            storage.banks = service.getBanksAndRates().toBanks()
            block()
        }
    }

    private suspend inline fun branchInfoIsReady(bankId: String, block: () -> Unit) {
        if (storage.branchesMap[bankId] != null) {
            block()
        } else {
            storage.branchesMap[bankId] = service.getBankDetails(bankId).toBranches()
            block()
        }
    }
}