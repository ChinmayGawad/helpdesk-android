/**
 * Secure session storage: encrypts/decrypts auth tokens and cookies using an
 * AES-256-GCM key stored in the Android Keystore (hardware-backed when available).
 */
package com.helpdesk.app.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Encrypts/decrypts secret session data (auth tokens, cookies, cached user)
 * using an AES-256-GCM key stored in the Android Keystore.
 *
 * The key is non-exportable and hardware-backed where available, so the
 * plaintext secrets never leave the app's secure storage unencrypted.
 */
object KeystoreCrypto {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "helpdesk_session_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH_BITS = 128

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    }

    private fun getOrCreateKey(): SecretKey {
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    /** Encrypts [plaintext] and returns a Base64-encoded "iv:ciphertext" string. */
    fun encrypt(plaintext: String): String? {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            val iv = cipher.iv
            val cipherBytes = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            val combined = iv + cipherBytes
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    /** Decrypts a string produced by [encrypt]. Returns null on failure. */
    fun decrypt(encoded: String?): String? {
        if (encoded.isNullOrBlank()) return null
        return try {
            val combined = Base64.decode(encoded, Base64.NO_WRAP)
            if (combined.size <= 12) return null // iv (12 bytes) + at least a tag
            val iv = combined.copyOfRange(0, 12)
            val cipherBytes = combined.copyOfRange(12, combined.size)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
            String(cipher.doFinal(cipherBytes), Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }
}