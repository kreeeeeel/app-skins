package com.project.app.service.steam

interface SteamGuard {
    fun getCode(sharedSecret: String): String
}