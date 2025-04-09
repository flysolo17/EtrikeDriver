package com.flysolo.etrikedriver.screens.auth.edit_profile

import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.utils.TextFieldData


data class EditProfileState(
    val isLoading : Boolean = false,
    val users : User? = null,
    val name : TextFieldData = TextFieldData(),
    val phone : TextFieldData = TextFieldData(),
    val errors : String ? = null,
    val isSaving : Boolean = false,
    val isDoneSaving : String ? = null
)