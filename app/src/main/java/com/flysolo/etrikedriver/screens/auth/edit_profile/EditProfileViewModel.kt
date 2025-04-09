package com.flysolo.etrikedriver.screens.auth.edit_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.repository.auth.AuthRepository
import com.flysolo.etrikedriver.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var state by mutableStateOf(EditProfileState())
    fun events(e : EditProfileEvents) {
        when(e) {
            is EditProfileEvents.OnNameChange -> nameChanged(e.name)

            is EditProfileEvents.OnSaveChanges -> saveChanges(
                e.uid,
                e.name,
                e.phone
            )
            is EditProfileEvents.OnPhoneChange -> phonChange(e.phone)
            is EditProfileEvents.OnSetUser -> state = state.copy(
                users = e.user
            )
        }
    }

    private fun phonChange(phone: String) {
        val isValid = phone.matches("^09\\d{9}\$".toRegex())
        val errorMessage = if (isValid) null else "Phone number must start with '09' and have 11 digits"
        state = state.copy(
            phone = state.phone.copy(
                value = phone,
                hasError = !isValid, // `hasError` is true when `isValid` is false
                errorMessage = errorMessage
            )
        )
    }

    private fun nameChanged(name : String) {
        val isValid = name.isNotBlank()
        val errorMessage = if (isValid) null else "Name cannot be empty"
        state = state.copy(
            name = state.name.copy(
                value = name,
                hasError = isValid,
                errorMessage = errorMessage
            )
        )
    }

    private fun saveChanges(uid: String, name: String,phone: String) {
        viewModelScope.launch {
            authRepository.updateUserInfo(uid,name,phone) {
                state = when(it) {
                    is UiState.Error -> state.copy(
                        isSaving = false,
                        errors = it.message
                    )
                    UiState.Loading -> state.copy(
                        isSaving = true,
                        errors = null,
                    )
                    is UiState.Success -> state.copy(
                        isSaving = false,
                        errors = null,
                        isDoneSaving = it.data
                    )
                }
            }
        }
    }
}