package com.project.app.models

import com.project.app.service.steam.SteamAuthClient
import com.project.app.service.steam.impl.DefaultSteamAuthClient

data class SteamModel(
    private val username: String,
    private val password: String,
    private val sharedSecret: String
) {

    var steamCookie: String? = null
    var steamId: String? = null

    var isLoggedIn: Boolean = false

    private val steamAuthClient: SteamAuthClient = DefaultSteamAuthClient()

    fun loggedIn(): Boolean {
        val fetchRSAParam = steamAuthClient.fetchRSAParam(username) ?: return false

        val beginAuth = steamAuthClient.beginAuth(username, password, fetchRSAParam) ?: return false
        if (beginAuth.isNullable()){
            return false
        }

        val id = beginAuth.steamId ?: return false
        val clientId = beginAuth.clientId ?: return false
        val requestId = beginAuth.requestId ?: return false

        if( !steamAuthClient.updateSessionWithSteamGuard(id, sharedSecret, clientId) ){
            return false
        }

        val pollLoginStatus = steamAuthClient.pollLoginStatus(clientId, requestId) ?: return false
        if (pollLoginStatus.isNullable()) {
            return false
        }

        val refreshToken = pollLoginStatus.refreshToken ?: return false
        val finalizeLogin = steamAuthClient.finalizeLogin(refreshToken) ?: return false

        val cookie = steamAuthClient.getCommunityCookie(finalizeLogin)
        if (cookie.isNullOrEmpty()){
            return false
        }

        steamCookie = cookie
        isLoggedIn = true
        steamId = id

        return true
    }

}
