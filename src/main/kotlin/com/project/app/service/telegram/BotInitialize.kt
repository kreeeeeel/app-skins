package com.project.app.service.telegram

import com.project.app.service.logger.Logger
import com.project.app.service.logger.impl.DefaultLogger
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

object BotInitialize {

    private val logger: Logger = DefaultLogger()

    var telegramBot: TelegramBot? = null

    fun init(token: String, botName: String){

        val bot = TelegramBot(token, botName)

        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
        logger.info("Попытка инициализации телеграм бота: $botName")
        try {
            telegramBotsApi.registerBot(bot)

            bot.isConnected = true
            telegramBot = bot

            logger.info("Телеграм бот успешно запущен!")
        } catch (e: Exception){
            bot.onClosing()

            telegramBot = null
            logger.error("Ошибка при запуске телеграм бота!")
        }

    }

}