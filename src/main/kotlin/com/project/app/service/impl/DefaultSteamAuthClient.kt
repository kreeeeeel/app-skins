package com.project.app.service.impl

import com.google.gson.GsonBuilder
import com.project.app.client.api.Authentication
import com.project.app.client.api.LoginFinalize
import com.project.app.client.api.Transfer
import com.project.app.client.response.RefreshTokenResponse
import com.project.app.client.response.SteamDataResponse
import com.project.app.client.response.TransferResponse
import com.project.app.service.PasswordEncryptor
import com.project.app.service.SteamAuthClient
import com.project.app.service.SteamGuard
import com.project.app.service.models.RSAParam
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigInteger
import java.security.spec.RSAPublicKeySpec
import java.util.*

class DefaultSteamAuthClient: SteamAuthClient {

    private val gsonConverterFactory = GsonConverterFactory.create(
        GsonBuilder()
            .setLenient()
            .create()
    )

    private val steamAuthClient = Retrofit.Builder()
        .baseUrl("https://api.steampowered.com/IAuthenticationService/")
        .addConverterFactory(gsonConverterFactory).build()

    private val steamLoginClient = Retrofit.Builder()
        .baseUrl("https://login.steampowered.com/")
        .addConverterFactory(gsonConverterFactory).build()

    private val steamAuthApi = steamAuthClient.create(Authentication::class.java)
    private val steamLoginApi = steamLoginClient.create(LoginFinalize::class.java)

    private val passwordEncryptor: PasswordEncryptor = DefaultPasswordEncryptor()
    private val steamGuard: SteamGuard = DefaultSteamGuard()

    override fun fetchRSAParam(username: String): RSAParam? {
        val response = steamAuthApi.getRSAPublicKey(username)
            .execute()

        val body = response.body()?.response ?: return null
        return RSAParam(
            RSAPublicKeySpec(
                BigInteger(body.publickeyMod, 16),
                BigInteger(body.publickeyExp, 16)
            ),
            body.timestamp.toLong()
        )
    }

    override fun beginAuth(username: String, pass: String, param: RSAParam): SteamDataResponse? {
        val encryptedPass = passwordEncryptor.encrypt(param.pubKeySpecval, pass) ?: return null
        val response = steamAuthApi.beginAuthSessionViaCredentials(
            username = username,
            encryptedPassword = encryptedPass,
            encryptionTimestamp = param.timestamp.toString()
        ).execute()

        val body = response.body() ?: return null
        return body.response
    }

    override fun updateSessionWithSteamGuard(steamId: String, sharedSecret: String, clientId: String): Boolean {
        val response = steamAuthApi.updateSessionWithSteamGuard(
            steamId = steamId,
            clientId = clientId,
            code = steamGuard.getCode(sharedSecret)
        ).execute()

        return response.body() != null
    }

    override fun pollLoginStatus(clientId: String, requestId: String): RefreshTokenResponse? {
        val response = steamAuthApi.pollAuthSessionStatus(clientId, requestId).execute()
        return response.body()?.response

    }

    override fun finalizeLogin(refreshToken: String): TransferResponse? {
        val response = steamLoginApi.finalizeLogin(refreshToken, getRandomHexString()).execute()
        return response.body()
    }

    // Доставать отсюда куки
    override fun upgradeCookie(transferResponse: TransferResponse) {
        transferResponse.transferInfo.forEach {

            val client = Retrofit.Builder()
                .baseUrl(it.url.replace("settoken", ""))
                .addConverterFactory(gsonConverterFactory).build()

            val api = client.create(Transfer::class.java)
            val execute = api.transfer(it.params.nonce, it.params.auth, transferResponse.steamID).execute()
            if (!execute.isSuccessful) {
                throw RuntimeException("хуйня какая-то")
            }

        }
    }

    private fun getRandomHexString(): String {
        val count = 12
        val random = Random()
        val stringBuffer = StringBuffer()

        while (stringBuffer.length < count) {
            stringBuffer.append(Integer.toHexString(random.nextInt()))
        }
        return stringBuffer.toString().substring(0, count)
    }
}