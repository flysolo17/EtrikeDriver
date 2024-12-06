package com.flysolo.etrikedriver.screens.main.bottom_nav.profile

import com.flysolo.etrikedriver.models.users.User


sealed interface ProfileEvents {
    data class OnSetUser(val user: User?) : ProfileEvents
    data object OnLogout : ProfileEvents
}