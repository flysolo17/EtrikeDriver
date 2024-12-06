package com.flysolo.etrikedriver.screens.auth.login


sealed interface LoginEvents  {
    data class SignInWithGoogle(val idToken : String): LoginEvents

    data class OnEmailChange(val email : String) : LoginEvents
    data class OnPasswordChange(val password : String) : LoginEvents
    data object OnTogglePasswordVisibility : LoginEvents

    data object OnGetCurrentUser : LoginEvents

    data object OnLogin : LoginEvents
}