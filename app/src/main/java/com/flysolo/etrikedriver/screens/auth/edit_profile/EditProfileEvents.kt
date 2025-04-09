package com.flysolo.etrikedriver.screens.auth.edit_profile

import com.flysolo.etrikedriver.models.users.User

sealed interface EditProfileEvents  {
    data class OnSetUser(val user: User ?) : EditProfileEvents
    data class OnNameChange(val name : String) : EditProfileEvents

    data class OnPhoneChange(val phone : String) : EditProfileEvents


    data class OnSaveChanges(
        val uid : String,
        val name : String,
        val phone : String,

        ) : EditProfileEvents
}