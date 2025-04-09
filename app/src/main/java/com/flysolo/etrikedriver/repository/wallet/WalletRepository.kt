package com.flysolo.etrikedriver.repository.wallet

import com.flysolo.etrikedriver.models.wallet.CashOutRequest
import com.flysolo.etrikedriver.models.wallet.Wallet
import com.flysolo.etrikedriver.models.wallet.WalletActivity
import com.flysolo.etrikedriver.models.wallet.WalletHistory
import com.flysolo.etrikedriver.utils.UiState


interface WalletRepository {
    suspend fun createWallet(
        wallet: Wallet
    ) : Result<String>

    suspend fun getMyWallet(
        id : String,
        result: (UiState<Wallet?>) -> Unit
    )

    suspend fun getWalletHistory(
        id : String,
        result: (UiState<List<WalletHistory>>) -> Unit
    )

    suspend fun getActivity(
        id : String,
        result: (UiState<List<WalletActivity>>) -> Unit
    )

    suspend fun cashOut(
         cashOutRequest: CashOutRequest,
    ) : Result<String>

}