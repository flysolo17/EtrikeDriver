package com.flysolo.etrikedriver.repository.auth

import android.util.Log
import com.flysolo.etrikedriver.models.contacts.CONTACT_COLLECTION
import com.flysolo.etrikedriver.models.contacts.Contacts

import com.flysolo.etrikedriver.models.users.UserWithVerification
import com.flysolo.etrikedriver.models.users.USER_COLLECTION
import com.flysolo.etrikedriver.models.users.User
import com.flysolo.etrikedriver.models.users.UserType
import com.flysolo.etrikedriver.utils.generateRandomNumberString

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    storage: FirebaseStorage,

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
                    val userWithVerification = UserWithVerification(
                        user = user!!,
                        isVerified = firebaseUser.isEmailVerified
                    )
                    Result.success(userWithVerification)
                } else {
                    Result.success(null)
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

}