package com.project.app

import com.project.app.models.ConfigModel
import com.project.app.service.logger.Logger
import com.project.app.service.logger.impl.DefaultLogger
import com.project.app.service.telegram.BotInitialize
import com.project.app.ui.component.notify.NotifyComponent
import com.project.app.ui.controller.AccountController
import javafx.application.Application
import javafx.application.Platform
import java.util.concurrent.CompletableFuture

const val TITLE = "Steam Farm"
const val DESCRIPTION = "Взаимодействие с Steam"

private val logger: Logger = DefaultLogger()

class Desktop

fun main() {
    logger.info("Запуск приложения")
    CompletableFuture.supplyAsync {
        val config = ConfigModel().init()
        config.telegram?.let {

            if (it.isConnected || it.isWaiting) {

                it.bot?.let { bot ->
                    if (bot.token != null && bot.name != null) {
                        BotInitialize.init(bot.token!!, bot.name!!)
                    }
                }

                if (BotInitialize.telegramBot == null) {
                    config.telegram = null
                    config.save()

                    Platform.runLater {
                        val notifyComponent = NotifyComponent()
                        notifyComponent.failure("Не удалось инициазировать телеграм бота, пожалуйста, укажите еще раз ваши данные от бота!")
                    }
                }
            }
        }

    }
    Application.launch(AccountController::class.java)
}