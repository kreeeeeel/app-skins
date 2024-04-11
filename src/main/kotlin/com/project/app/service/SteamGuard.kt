package com.project.app.service

interface SteamGuard {
    fun getCode(sharedSecret: String): String
}