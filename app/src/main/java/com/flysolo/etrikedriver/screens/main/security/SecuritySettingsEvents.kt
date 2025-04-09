package com.flysolo.etrikedriver.screens.main.security


sealed interface SecuritySettingsEvents {
    data object OnGetUser : SecuritySettingsEvents
    data object OnEnableBiometrics : SecuritySettingsEvents
}