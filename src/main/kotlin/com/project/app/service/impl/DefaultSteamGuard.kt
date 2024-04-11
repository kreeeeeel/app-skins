package com.project.app.service.impl

import com.google.gson.GsonBuilder
import com.project.app.client.api.TwoFactorService
import com.project.app.service.SteamGuard
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.ByteBuffer
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val symbols = "23456789BCDFGHJKMNPQRTVWXY"

class DefaultSteamGuard: SteamGuard {

    private val client = Retrofit.Builder()
        .baseUrl("https://api.steampowered.com/ITwoFactorService/")
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            )
        ).build()

    private val steamTimeApi: TwoFactorService = client.create(TwoFactorService::class.java)

    override fun getCode(sharedSecret: String): String {
        val timestamp = System.currentTimeMillis() / 1000 + getQueryTime()
        val hmac = hmacSha1(sharedSecret, timestamp / 30)
        val ord = hmac[19].toInt() and 0xF
        val value = ByteBuffer.wrap(hmac.copyOfRange(ord, ord + 4)).int and 0x7FFFFFFF
        val codeBuilder = StringBuilder()
        var tempValue = value
        repeat(5) {
            val symbolIndex = tempValue % symbols.length
            codeBuilder.append(symbols[symbolIndex])
            tempValue /= symbols.length
        }
        return codeBuilder.toString()
    }

    private fun getQueryTime(): Long {
        return steamTimeApi.getTime().execute().body()?.serverTime?.toLong() ?: return 0
    }


    private fun hmacSha1(key: String, value: Long): ByteArray {
        val mac: Mac
        try {
            mac = Mac.getInstance("HmacSHA1")
            val secretKey = SecretKeySpec(Base64.getDecoder().decode(key), "HmacSHA1")
            mac.init(secretKey)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to create HMAC-SHA1 instance", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Invalid key for HMAC-SHA1", e)
        }
        return mac.doFinal(ByteBuffer.allocate(java.lang.Long.SIZE / java.lang.Byte.SIZE).putLong(value).array())
    }
}