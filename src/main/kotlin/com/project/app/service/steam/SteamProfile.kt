package com.project.app.service.steam

import com.project.app.client.response.SteamProfileResponse

interface SteamProfile {
    fun getProfileData(steamId: String): SteamProfileResponse?
    fun getTradeLink(steamId: String, cookie: String): String?
}