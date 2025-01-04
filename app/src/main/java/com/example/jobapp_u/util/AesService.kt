package com.example.jobapp_u.util

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class AesService {

    private val keyAes = "JobAppAdmin12025"

    // Lấy khóa AES từ Keystore
    private fun getAesKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        if (!keyStore.containsAlias(keyAes)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAes,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            return keyGenerator.generateKey()
        }

        return keyStore.getKey(keyAes, null) as SecretKey
    }

    // Mã hóa dữ liệu
    @RequiresApi(Build.VERSION_CODES.O)
    fun encryptFieldData(data: String): String {
        val secretKey = SecretKeySpec(keyAes.toByteArray(Charsets.UTF_8), "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val iv = cipher.iv
        val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        val encrypted = iv + encryptedData;
        return Base64.getEncoder().encodeToString(encrypted)
    }

    // Giải mã dữ liệu
    @RequiresApi(Build.VERSION_CODES.O)
    fun decryptFieldData(encryptedData: String): String {
        return try {
            val secretKey = SecretKeySpec(keyAes.toByteArray(Charsets.UTF_8), "AES")
            val data = Base64.getDecoder().decode(encryptedData)

            val iv = data.copyOfRange(0, 12)
            val encryptedText = data.copyOfRange(12, data.size)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decrypted = cipher.doFinal(encryptedText)

            String(decrypted, Charsets.UTF_8)
        }catch (e: Exception) {
            e.printStackTrace()
            encryptedData
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isDecryptionSuccessful(encryptedData: String): Boolean {
        return try {
            decryptFieldData(encryptedData)
            true
        } catch (e: Exception) {
            e.printStackTrace() // Ghi log lỗi nếu cần
            false
        }
    }
}