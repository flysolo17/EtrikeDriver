package com.flysolo.etrikedriver.repository.auth

import android.app.Activity
import android.net.Uri
import com.flysolo.etrikedriver.models.contacts.Contacts
import com.flysolo.etrikedriver.models.users.Pin

import com.flysolo.etrikedriver.models.users.UserWithVerification
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.utils.UiState
import com.google.firebase.auth.PhoneAuthProvider


interface AuthRepository {
    suspend fun signInWithGoogle(
        idToken : String
    ) : Result<UserWithVerification?>

    suspend fun signInWithEmailAndPassword(
        email : String,
        password : String,
    ) : Result<UserWithVerification?>
    suspend fun saveUser(user: User)

    suspend fun getCurrentUser() : Result<UserWithVerification?>
    suspend fun register(
        user: User,
        password: String,
        contacts : List<Contacts>
    ) : Result<String>

    suspend fun logout()




    suspend fun sendEmailVerification() : Result<String>

    suspend fun listenToUserEmailVerification() : Result<Boolean>

    suspend fun forgotPassword(email : String) : Result<String>


    suspend fun getUser(id : String) : Result<User?>



    suspend fun changePassword(oldPassword : String,newPassword : String,result: (UiState<String>) -> Unit)

    suspend fun deleteAccount(
        uid : String,
        password : String,
        result: (UiState<String>) -> Unit
    )

    suspend fun changeProfile(
        uid : String,
        uri: Uri,
        result: (UiState<String>) -> Unit
    )

    suspend fun updateUserInfo(
        uid : String,
        name : String,
        phone : String,
        result: (UiState<String>) -> Unit
    )

    suspend fun getUserByID(
        id : String,
        result: (UiState<User?>) -> Unit
    )

    suspend fun getCurrentUserInRealtime(result: (UiState<User?>) -> Unit)



    suspend fun OnChangePin(
        id: String,
        pin: Pin
    ) : Result<String>

    suspend fun OnBiometricEnabled(
        id: String,
        pin: Pin
    ) : Result<String>




    suspend fun sendOtp(
        activity: Activity,
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )
    suspend fun verifyOtp(
        verificationId: String,
        otp: String
    ) : Result<Boolean>

}