package com.flysolo.etrikedriver.utils


sealed class UiState<out T> {
    data class Success<T>(val data : T) : UiState<T>()
    data class Error(val message : String) : UiState<Nothing>()
    data object Loading: UiState<Nothing>()
}