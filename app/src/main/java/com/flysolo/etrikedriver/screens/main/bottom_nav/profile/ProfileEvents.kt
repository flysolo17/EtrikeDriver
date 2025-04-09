package com.flysolo.etrikedriver.screens.main.bottom_nav.profile

import android.net.Uri
import com.flysolo.etrikedriver.models.users.User


sealed interface ProfileEvents {
    data object OnGetUserByID : ProfileEvents
    data object OnLogout : ProfileEvents


    data class OnDeleteAccount(
        val password : String
    ): ProfileEvents
    data class ChangeProfile(val uri : Uri) : ProfileEvents


}