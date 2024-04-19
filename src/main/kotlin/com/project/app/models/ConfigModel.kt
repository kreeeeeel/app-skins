package com.project.app.models

import com.google.gson.GsonBuilder
import com.project.app.data.type.BrowserType
import com.project.app.service.logger.Logger
import com.project.app.service.logger.impl.DefaultLogger
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets

private const val FILE_CONFIG = "config.json"

data class Telegram(
    var isConnected: Boolean = false,
    var isWaiting: Boolean = false,
    var bot: TelegramBot? = null,
    var user: TelegramUser? = null,
)

data class TelegramBot(
    var token: String? = null,
    var name: String? = null,
    var code: String? = null,
)

data class TelegramUser(
    var name: String? = null,
    var username: String? = null,
    var photo: String? = null,
    var chatId: String? = null,
)

data class ConfigModel(
    var attemptRequest: Int = 3,
    var isEnabledTray: Boolean? = null,
    var browserType: BrowserType = BrowserType.NONE,
    var telegram: Telegram? = null
) {

    @Transient private val logger: Logger = DefaultLogger()
    @Transient private val gson = GsonBuilder().setPrettyPrinting().create()

    fun init(): ConfigModel {
        try {
            val file = File(FILE_CONFIG)
            if (!file.exists() && file.createNewFile()) {
                logger.info("Файл с конфигурацией приложения создан!")
            }

            val import = gson.fromJson(FileReader(file), ConfigModel::class.java)

            attemptRequest = import.attemptRequest
            isEnabledTray = import.isEnabledTray
            browserType = import.browserType
            telegram = import.telegram
        } catch (ignored: Exception) {
            logger.error("Не удалось инициализировать конфигурацию. Загружен стандартный файл")
        }

        return this
    }

    fun save() {
        FileWriter(FILE_CONFIG, StandardCharsets.UTF_8).use {
            it.write(gson.toJson(this))
            it.flush()
        }
    }

}
