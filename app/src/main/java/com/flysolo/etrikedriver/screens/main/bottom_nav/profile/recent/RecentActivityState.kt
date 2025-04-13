package com.flysolo.etrikedriver.screens.main.bottom_nav.profile.recent

import com.flysolo.etrikedriver.models.wallet.WalletActivity

data class RecentActivityState(
    val isLoading : Boolean = false,
    val activities : List<WalletActivity> = emptyList(),
    val errors : String? = null,
)
