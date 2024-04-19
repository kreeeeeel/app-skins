package com.project.app.service.telegram

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

class TelegramBot(
    private val token: String,
    private val botName: String
): TelegramLongPollingBot (token) {


    var isConnected: Boolean = false

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(p0: Update?) {
        TODO("Not yet implemented")
    }
}