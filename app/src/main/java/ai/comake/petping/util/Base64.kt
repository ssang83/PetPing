package ai.comake.petping.util

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun Double.encrypt(): String {
    val key = "9C8vAFMBNwtgSnzJM85uz/ORI6ian7aN"
    val iv = key.substring(0, 16)

    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(
        Cipher.ENCRYPT_MODE,
        SecretKeySpec(key.toByteArray(), "AES"),
        IvParameterSpec(iv.toByteArray())
    )
    return Base64.getEncoder().encodeToString(cipher.doFinal(this.toString().toByteArray()))
}

fun String.decrypt(): Double {
    val key = "9C8vAFMBNwtgSnzJM85uz/ORI6ian7aN"
    val iv = key.substring(0, 16)

    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(
        Cipher.DECRYPT_MODE,
        SecretKeySpec(key.toByteArray(), "AES"),
        IvParameterSpec(iv.toByteArray())
    )
    return String(cipher.doFinal(Base64.getDecoder().decode(this))).toDouble()
}