package com.project.app.service

import com.project.app.client.response.RefreshTokenResponse
import com.project.app.client.response.SteamDataResponse
import com.project.app.client.response.TransferResponse
import com.project.app.service.models.RSAParam

interface SteamAuthClient {
    fun fetchRSAParam(username: String): RSAParam?
    fun beginAuth(username: String, pass: String, param: RSAParam): SteamDataResponse?
    fun updateSessionWithSteamGuard(steamId: String, sharedSecret: String, clientId: String): Boolean
    fun pollLoginStatus(clientId: String, requestId: String): RefreshTokenResponse?
    fun finalizeLogin(refreshToken: String): TransferResponse?
    fun upgradeCookie(transferResponse: TransferResponse)
}