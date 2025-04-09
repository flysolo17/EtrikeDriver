package com.flysolo.etrikedriver.screens.wallet

import com.flysolo.etrikedriver.models.wallet.Wallet
import com.flysolo.etrikedriver.models.wallet.WalletActivity


data class WalletState(
    val isLoading : Boolean = false,
    val wallet : Wallet? = null,
    val errors : String ? = null,
    val activity : WalletActivityData = WalletActivityData()
)

data class WalletActivityData(
    val isLoading : Boolean = false,
    val data : List<WalletActivity> = emptyList(),
    val errors : String ? = null,
)