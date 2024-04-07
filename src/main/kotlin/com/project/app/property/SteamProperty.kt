package com.project.app.property

import com.google.gson.annotations.SerializedName
import com.project.app.client.api.SteamApi
import retrofit2.Retrofit
import java.nio.ByteBuffer
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val symbols = "23456789BCDFGHJKMNPQRTVWXY"
private val client = Retrofit.Builder()
    .baseUrl("https://api.steampowered.com/")
    .build()

private val steamApi: SteamApi = client.create(SteamApi::class.java)

data class SteamProperty(
    @SerializedName("shared_secret") val sharedSecret: String,
    @SerializedName("serial_number") val serialNumber: String,
    @SerializedName("revocation_code") val revocationCode: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("server_time") val serverTime: Long,
    @SerializedName("account_name") val accountName: String,
    @SerializedName("token_gid") val tokenGid: String,
    @SerializedName("identity_secret") val identitySecret: String,
    @SerializedName("secret_1") val secret1: String,
    @SerializedName("status") val status: Int,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("fully_enrolled") val fullyEnrolled: Boolean,
    @SerializedName("Session") val session: Session
) {
    fun getSteamGuard(): String {
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
        try {
            val execute = steamApi.getServerTime().execute()
            return execute.body()!!.serverTime.toLong()
        } catch (exception: Exception) {
            return 0
        }
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

data class Session(
    @SerializedName("SteamID") val steamID: Long,
    @SerializedName("AccessToken") val accessToken: String,
    @SerializedName("RefreshToken") val refreshToken: String,
    @SerializedName("SessionID") val sessionID: String?
)
