package com.powersoft.common.utils

import android.util.Base64
import java.security.KeyFactory
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object EncryptionHelper {

    private const val AES_ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val RSA_ALGORITHM = "RSA/ECB/PKCS1Padding"

    fun encryptPayloadWithPublicKey(payload: String, publicKeyString: String): Pair<String, String> {
        val aesKey = generateAESKey()
        val iv = generateRandomIV()

        val encryptedPayload = encryptWithAES(payload, aesKey, iv)

        val publicKey = getPublicKeyFromString(publicKeyString)
        val encryptedAESKey = encryptAESKeyWithRSA(aesKey, publicKey)

        return Pair(encryptedPayload, encryptedAESKey)
    }

    private fun encryptWithAES(data: String, secretKey: SecretKey, iv: ByteArray): String {
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    private fun encryptAESKeyWithRSA(secretKey: SecretKey, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance(RSA_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedKey = cipher.doFinal(secretKey.encoded)
        return Base64.encodeToString(encryptedKey, Base64.DEFAULT)
    }

    private fun generateAESKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        return keyGen.generateKey()
    }

    private fun generateRandomIV(): ByteArray {
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        return iv
    }

    private fun getPublicKeyFromString(publicKeyString: String): PublicKey {
        val publicKeyPEM: String = publicKeyString
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s", "")
        val keyBytes: ByteArray = Base64.decode(publicKeyPEM, Base64.NO_WRAP)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }
}
