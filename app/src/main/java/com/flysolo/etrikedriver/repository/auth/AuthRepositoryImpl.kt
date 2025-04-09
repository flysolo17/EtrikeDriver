package com.flysolo.etrikedriver.repository.auth

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.compose.material3.rememberModalBottomSheetState
import com.flysolo.etrikedriver.models.contacts.CONTACT_COLLECTION
import com.flysolo.etrikedriver.models.contacts.Contacts
import com.flysolo.etrikedriver.models.users.Pin

import com.flysolo.etrikedriver.models.users.UserWithVerification
import com.flysolo.etrikedriver.models.users.USER_COLLECTION
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.models.users.UserType
import com.flysolo.etrikedriver.utils.UiState
import com.flysolo.etrikedriver.utils.generateRandomNumberString
import com.flysolo.etrikedriver.utils.generateRandomString
import com.google.firebase.FirebaseException
import com.google.firebase.auth.EmailAuthProvider

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private  val storage: FirebaseStorage,

    ): AuthRepository {
    override suspend fun signInWithGoogle(idToken: String): Result<UserWithVerification?> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Authentication failed")

            val user = User(
                id = firebaseUser.uid,
                name = firebaseUser.displayName,
                email = firebaseUser.email,
                type = UserType.PASSENGER,
                phone = firebaseUser.phoneNumber,
                profile = firebaseUser.photoUrl?.toString()
            )
            val userWithVerification = UserWithVerification(
                user = user,
                isVerified = firebaseUser.isEmailVerified
            )
            saveUser(user)

            Result.success(userWithVerification)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<UserWithVerification?> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser == null) {
                Result.success(null)
            } else {
                val userDocument = firestore.collection(USER_COLLECTION)
                    .document(firebaseUser.uid)
                    .get()
                    .await()
                if (userDocument.exists()) {
                    val user = userDocument.toObject(User::class.java)
                    if (user?.type == UserType.PASSENGER) {
                        auth.signOut()
                        Result.failure(Error("User Not Found!"))
                    } else {
                        val userWithVerification = UserWithVerification(
                            user = user!!,
                            isVerified = firebaseUser.isEmailVerified
                        )
                        Result.success(userWithVerification)
                    }

                } else {
                    Result.failure(Error("User Not Found!"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun saveUser(user: User) {
        try {
            val userRef = firestore.collection(USER_COLLECTION).document(user.id!!)
            val documentSnapshot = userRef.get().await()

            if (!documentSnapshot.exists()) {
                userRef.set(user).await()
            }
        } catch (e: Exception) {
            throw Exception("Failed to save user: ${e.localizedMessage}")
        }
    }


    override suspend fun getCurrentUser(): Result<UserWithVerification?> {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val userRef = firestore.collection(USER_COLLECTION).document(uid)
                val documentSnapshot = userRef.get().await()
                val user = documentSnapshot.toObject(User::class.java)
                delay(1000)
                val userWithVerification = UserWithVerification(
                    user = user!!,
                    isVerified = auth.currentUser?.isEmailVerified ?: false
                )
                Result.success(userWithVerification)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    override suspend fun register(user: User, password: String, contacts: List<Contacts>): Result<String> {
        return try {
            val firestoreBatch = firestore.batch()
            val authResult = auth.createUserWithEmailAndPassword(user.email!!, password).await()
            val userId = authResult.user?.uid ?: throw IllegalStateException("User ID not found after registration")
            user.id = userId
            val userRef = firestore.collection(USER_COLLECTION).document(userId)
            firestoreBatch.set(userRef, user)
            contacts.forEach { contact ->
                val contactRef = firestore
                    .collection(USER_COLLECTION)
                    .document(userId)
                    .collection(CONTACT_COLLECTION)
                    .document(contact.id ?: generateRandomNumberString())
                firestoreBatch.set(contactRef, contact)
            }
            firestoreBatch.commit().await()
            Result.success("Successfully Created!")
        } catch (e: Exception) {
            Log.d("register",e.message,e)
            Result.failure(e)
        }
    }



    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun sendEmailVerification(): Result<String> {
        return try {
            val currentUser = auth.currentUser

            if (currentUser != null) {
                if (currentUser.isEmailVerified) {
                    Result.success("User Verified.")
                } else {
                    currentUser.sendEmailVerification().await()
                    Result.success("Verification email sent successfully.")
                }

            } else {
                Result.failure(Exception("No authenticated user found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun listenToUserEmailVerification(): Result<Boolean> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Reload the user to get the latest email verification status
                currentUser.reload().await()
                val isVerified = currentUser.isEmailVerified
                Result.success(isVerified)
            } else {
                Result.failure(Exception("No authenticated user found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forgotPassword(email: String): Result<String> {
        return try {

            if (email.isEmpty()) {
                return Result.failure(Exception("Email cannot be empty."))
            }


            auth.sendPasswordResetEmail(email).await()


            Result.success("Password reset email sent successfully.")
        } catch (e: FirebaseAuthInvalidUserException) {

            Result.failure(Exception("No account found with this email address."))
        } catch (e: Exception) {

            Result.failure(Exception("Failed to send password reset email: ${e.message}"))
        }
    }

    override suspend fun getUser(id: String): Result<User?> {
        return try {
            val result = firestore.collection(USER_COLLECTION).document(id).get().await().toObject<User>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        result: (UiState<String>) -> Unit
    ) {
        try {
            result.invoke(UiState.Loading)
            val currentUser = auth.currentUser

            if (currentUser != null) {
                val credential = EmailAuthProvider.getCredential(currentUser.email!!, oldPassword)
                currentUser.reauthenticate(credential).await()
                currentUser.updatePassword(newPassword).await()

                result.invoke(UiState.Success("Password updated successfully."))
            } else {

                result.invoke(UiState.Error("User is not logged in."))
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            result.invoke(UiState.Error("Old password is incorrect."))
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.message.toString()))
        }
    }

    override suspend fun deleteAccount(uid: String,password: String, result: (UiState<String>) -> Unit) {
        try {
            result(UiState.Loading)
            val user = auth.currentUser
            if (user != null) {
                val email = user.email
                val credential = EmailAuthProvider.getCredential(email!!, password)
                user.reauthenticate(credential).await()
                user.delete().await()
            }

            val userDocRef = firestore.collection(USER_COLLECTION).document(uid)
            userDocRef.delete().await()
            result(UiState.Success("Account deleted successfully"))
        } catch (e: Exception) {
            result(UiState.Error(e.localizedMessage ?: "Failed to delete account"))
        }
    }


    override suspend fun changeProfile(uid: String, uri: Uri, result: (UiState<String>) -> Unit) {
        try {
            result(UiState.Loading)


            val storageReference = storage.reference
            val profilePictureRef = storageReference.child("profile_pictures/${generateRandomString(6)}.jpg")

            val uploadTask = profilePictureRef.putFile(uri).await()


            val downloadUrl = profilePictureRef.downloadUrl.await()

            firestore.collection(USER_COLLECTION)
                .document(uid)
                .update("profile",downloadUrl)
                .await()
            result(UiState.Success("Profile picture updated: $downloadUrl"))

        } catch (exception: Exception) {

            result(UiState.Error("Error: ${exception.message}"))
        } catch (e : FirebaseException) {

            result(UiState.Error("Error: ${e.message}"))
        }
    }

    override suspend fun updateUserInfo(
        uid: String,
        name: String,
        phone: String,
        result: (UiState<String>) -> Unit
    ) {
        try {
            // Assume you have a Firestore instance to update user info in a "users" collection
            val userDocRef = firestore.collection(USER_COLLECTION).document(uid)

            // Update the user document with the new info
            userDocRef.update(
                mapOf(
                    "name" to name,
                    "phone" to phone
                )
            ).await()

            result(UiState.Success("User information updated successfully!"))
        } catch (e: Exception) {
            result(UiState.Error(e.localizedMessage ?: "Failed to update user info"))
        }
    }

    override suspend fun getUserByID(id: String, result: (UiState<User?>) -> Unit) {
        result.invoke(UiState.Loading)
        firestore.collection(
            USER_COLLECTION
        ).document(id)
            .addSnapshotListener { value, error ->
                error?.let {
                    result(UiState.Error(it.message.toString()))
                }
                value?.let {
                    val data = it.toObject<User>()
                    result(UiState.Success(data))
                }
            }
    }

    override suspend fun getCurrentUserInRealtime(result: (UiState<User?>) -> Unit) {
        result(UiState.Loading)
        val uid = auth.currentUser?.uid
        if (uid.isNullOrEmpty()) {
            result(UiState.Error("User Not Found!"))
            return
        }
        firestore.collection(USER_COLLECTION)
            .document(uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    result(UiState.Error(error.message ?: "Unknown Error"))
                    return@addSnapshotListener
                }

                if (value != null && value.exists()) {
                    val user = value.toObject(User::class.java)
                    if (user?.active == false) {
                        // Use a coroutine to update the active field
                        GlobalScope.launch {
                            try {
                                firestore.collection(USER_COLLECTION).document(uid).update("active", true).await()
                            } catch (e: Exception) {
                                result(UiState.Error("Failed to update user status: ${e.localizedMessage}"))
                                return@launch
                            }
                            result(UiState.Success(user))
                        }
                    } else {
                        result(UiState.Success(user))
                    }
                } else {
                    result(UiState.Success(null))
                }
            }
    }

    override suspend fun OnChangePin(id: String, pin: Pin): Result<String> {
        return try {
            // Attempt to update the pin in Firestore
            firestore.collection(USER_COLLECTION)
                .document(id)
                .update("pin", pin)
                .await()

            // Return success if the update is successful
            Result.success("PIN updated successfully")
        } catch (e: Exception) {
            // Handle errors and return a failure result
            Result.failure(e)
        }
    }


    override suspend fun OnBiometricEnabled(id: String, pin: Pin): Result<String> {
        return try {

            firestore.collection(USER_COLLECTION)
                .document(id)
                .update("pin", pin)
                .await()


            Result.success("Biometric authentication status updated successfully")
        } catch (e: Exception) {
            // Handle errors and return a failure result
            Result.failure(e)
        }
    }


    override suspend fun sendOtp(
        activity: Activity,
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+63$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun verifyOtp(
        verificationId: String, otp: String
    ): Result<Boolean> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            Result.success(true)
        } catch (e: Exception) {
            Result.success(false)
        }
    }

}