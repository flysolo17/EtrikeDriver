package com.flysolo.etrikedriver.services.pin

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.flysolo.etrikedriver.BuildConfig
import java.security.Key

import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

//
//class PinEncryptionManager constructor(
//    private val context: Context
//) {
//    private val secretAlias = "${BuildConfig.PIN_KEY}"
//    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
//
//    fun encrypt(pin: String): String {
//        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
//        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
//        val iv = cipher.iv
//        val encryptedData = cipher.doFinal(pin.toByteArray())
//
//
//        val combined = iv + encryptedData
//        return Base64.encodeToString(combined, Base64.DEFAULT)
//    }
//
//    fun decrypt(encryptedPin: String): String {
//        val combined = Base64.decode(encryptedPin, Base64.DEFAULT)
//
//        val iv = combined.sliceArray(0 until 16)
//        val encryptedData = combined.sliceArray(16 until combined.size)
//
//        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
//        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), IvParameterSpec(iv))
//        val decryptedData = cipher.doFinal(encryptedData)
//
//        return String(decryptedData)
//    }
//
//    private fun getOrCreateSecretKey(): SecretKey {
//        return keyStore.getKey(secretAlias, null) as? SecretKey ?: createSecretKey()
//    }
//
//    private fun createSecretKey(): SecretKey {
//        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
//        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
//            secretAlias,
//            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
//        )
//            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
//            .setUserAuthenticationRequired(false)
//            .build()
//
//        keyGenerator.init(keyGenParameterSpec)
//        return keyGenerator.generateKey()
//    }
//}


class PinEncryptionManager(private val context: Context) {

    private val key = BuildConfig.PIN_KEY
    private val algorithm = "AES"
    private val charset = Charsets.UTF_8

    private fun generateKey(): Key {
        return SecretKeySpec(key.toByteArray(charset), algorithm)
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, generateKey())
        val encryptedData = cipher.doFinal(data.toByteArray(charset))
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    fun decrypt(encryptedData: String): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, generateKey())
        val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)
        val decryptedData = cipher.doFinal(decodedData)
        return String(decryptedData, charset)
    }
}
