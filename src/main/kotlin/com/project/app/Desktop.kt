package com.project.app

import com.project.app.repository.ProfileRepository
import com.project.app.service.SteamAuthClient
import com.project.app.service.impl.DefaultSteamAuthClient
import com.project.app.ui.controller.AccountController
import javafx.application.Application

class Desktop

fun main() {
    val profileRepository = ProfileRepository()
    val profileProperty = profileRepository.findAll()[0]

    val steamAuthClient: SteamAuthClient = DefaultSteamAuthClient()
    val fetchRSAParam = steamAuthClient.fetchRSAParam(profileProperty.steam!!.accountName)
    val beginAuth = steamAuthClient.beginAuth(profileProperty.steam!!.accountName, profileProperty.password, fetchRSAParam!!)
    val update = steamAuthClient.updateSessionWithSteamGuard(beginAuth!!.steamId, profileProperty.steam!!.sharedSecret, beginAuth.clientId)

    val pollLoginStatus = steamAuthClient.pollLoginStatus(beginAuth.clientId, beginAuth.requestId)
    val finalizeLogin = steamAuthClient.finalizeLogin(pollLoginStatus!!.refreshToken)

    steamAuthClient.upgradeCookie(finalizeLogin!!)

    Application.launch(AccountController::class.java)
}