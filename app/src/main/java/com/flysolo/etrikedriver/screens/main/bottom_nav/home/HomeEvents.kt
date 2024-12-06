package com.flysolo.etrikedriver.screens.main.bottom_nav.home

import com.flysolo.etrikedriver.models.users.User


sealed interface HomeEvents {
    data class OnSetUser(
        val user: User ?
    ) : HomeEvents
}