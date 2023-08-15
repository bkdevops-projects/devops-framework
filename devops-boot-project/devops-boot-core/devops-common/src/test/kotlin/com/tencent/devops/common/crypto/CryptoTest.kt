package com.tencent.devops.common.crypto

import com.tencent.bk.sdk.crypto.util.SM4Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.random.Random

class CryptoTest {
    @Test
    fun sm4() {
        val plainBytes = Random.nextBytes(10)
        val key = "key"
        val sM4Factory = CipherFactoryProducer.getFactory("SM4")
        // 加密
        val encryptInputStream = sM4Factory.getEncryptInputStream(ByteArrayInputStream(plainBytes), key)
        val cryptoOutputStream = ByteArrayOutputStream()
        encryptInputStream.copyTo(cryptoOutputStream)

        // 解密
        val plainInputStream =
            sM4Factory.getPlainInputStream(ByteArrayInputStream(cryptoOutputStream.toByteArray()), key)
        val plainOutputStream = ByteArrayOutputStream()
        plainInputStream.copyTo(plainOutputStream)

        Assertions.assertArrayEquals(plainBytes, plainOutputStream.toByteArray())

        // output加密
        val encryptOutputStream = ByteArrayOutputStream()
        sM4Factory.getEncryptOutputStream(encryptOutputStream, key).use {
            ByteArrayInputStream(plainBytes).copyTo(it)
        }
        val decryptBytes = SM4Util.decrypt(key.toByteArray(), encryptOutputStream.toByteArray())
        Assertions.assertArrayEquals(plainBytes, decryptBytes)
    }
}