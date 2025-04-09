package com.flysolo.etrikedriver.screens.auth.phone

import android.app.Activity
import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrikedriver.models.wallet.Wallet
import com.flysolo.etrikedriver.repository.auth.AuthRepository
import com.flysolo.etrikedriver.repository.wallet.WalletRepository

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PhoneViewModel @Inject constructor(
    val authRepository: AuthRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {
    var state by mutableStateOf(PhoneState())


    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var countDownTimer: CountDownTimer? = null

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        }

        override fun onVerificationFailed(e: FirebaseException) {
            state = state.copy(errors = e.localizedMessage, isLoading = false)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            resendToken = token
            state = state.copy(verificationId = verificationId, isLoading = false)
            startCountdown()
        }
    }

    fun events(e : PhoneEvents) {
        when(e) {
            is PhoneEvents.OnSendOTP -> sendOtp(e.activity,e.phone)
            is PhoneEvents.OnSetUser -> state = state.copy(
                user = e.user,

            )

            is PhoneEvents.OnOtpChange ->
                state = state.copy(
                    otp =  e.text
                )
            PhoneEvents.OnVerifyOtp ->verifyOtp()
            is PhoneEvents.OnPhoneChange -> state = state.copy(
                phone = e.text
            )

            is PhoneEvents.OnSaveWallet -> saveWallet(e.phone,e.email)
        }
    }

    private fun saveWallet(phone: String, email: String) {
        val wallet : Wallet = Wallet(
            id = state.user?.id,
            email = email,
            phone = phone,
            amount = 0.00,
            name = state.user?.name
        )
        viewModelScope.launch {
            walletRepository.createWallet(wallet)
        }
    }


    private fun sendOtp(
        activity: Activity,
        phone : String
    ) {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            authRepository.sendOtp(activity,phone, callbacks)
        }
    }

    private fun verifyOtp() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            state.verificationId?.let { verificationId ->
                authRepository.verifyOtp(verificationId, state.otp).onSuccess {
                    state = state.copy(isVerified = it, isLoading = false)
                    events(
                        PhoneEvents.OnSaveWallet(
                        phone = state.phone,
                        email =state.user?.email ?: ""
                    ))
                }.onFailure {
                    state = state.copy(errors = it.message, isLoading = false)
                }
            }
        }
    }


    fun startCountdown() {
        countDownTimer?.cancel()
        state = state.copy(countdown = 60)
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                state = state.copy(countdown = (millisUntilFinished / 1000).toInt())
            }
            override fun onFinish() {
                stopCountdown()
            }
        }.start()
    }


    fun stopCountdown() {
        countDownTimer?.cancel()
        state = state.copy(countdown = 0)
    }
}