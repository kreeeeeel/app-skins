package com.project.app.service.steam.impl

import com.google.gson.GsonBuilder
import com.project.app.client.api.Authentication
import com.project.app.client.api.LoginFinalize
import com.project.app.client.api.Transfer
import com.project.app.client.response.RefreshTokenResponse
import com.project.app.client.response.SteamDataResponse
import com.project.app.client.response.TransferResponse
import com.project.app.service.encrypto.PasswordEncryptor
import com.project.app.service.encrypto.impl.DefaultPasswordEncryptor
import com.project.app.models.RSAParam
import com.project.app.service.steam.SteamAuthClient
import com.project.app.service.steam.SteamGuard
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigInteger
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
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

    private val steamTransferClient = Retrofit.Builder()
        .baseUrl("https://steamcommunity.com/login/")
        .addConverterFactory(gsonConverterFactory).build()

    private val steamAuthApi = steamAuthClient.create(Authentication::class.java)
    private val steamLoginApi = steamLoginClient.create(LoginFinalize::class.java)
    private val steamTransferApi = steamTransferClient.create(Transfer::class.java)

    private val passwordEncryptor: PasswordEncryptor = DefaultPasswordEncryptor()
    private val steamGuard: SteamGuard = DefaultSteamGuard()

    override fun fetchRSAParam(username: String): RSAParam? {
        val response = steamAuthApi.getRSAPublicKey(username = username)
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
        val response = steamAuthApi.pollAuthSessionStatus(clientId = clientId, requestId = requestId).execute()
        return response.body()?.response

    }

    override fun finalizeLogin(refreshToken: String): TransferResponse? {
        val response = steamLoginApi.finalizeLogin(nonce = refreshToken, sessionId = getRandomHexString()).execute()
        return response.body()
    }

    override fun getCommunityCookie(transferResponse: TransferResponse): String? {

        val transferInfo = transferResponse.transferInfo.firstOrNull {
            it.url.startsWith(
                steamTransferClient.baseUrl().url().toString()
            )
        } ?: return null

        val response = steamTransferApi.transfer(
            nonce = transferInfo.params.nonce,
            auth = transferInfo.params.auth,
            steamId = transferResponse.steamID
        ).execute()

        val cookie = response.headers()["Set-Cookie"] ?: return null
        val index = cookie.indexOf(";")
        if (index == -1) {
            return null
        }

        return URLDecoder.decode(cookie.substring(0, index), StandardCharsets.UTF_8)
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