package com.project.app.models

import com.google.gson.GsonBuilder
import com.project.app.service.logger.Logger
import com.project.app.service.logger.impl.DefaultLogger
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets

private const val FILE_CONFIG = "config.json"

data class ConfigModel(
    var attemptRequest: Int = 3,
    var isEnabledTray: Boolean? = null,
) {

    private val logger: Logger = DefaultLogger()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    init {
        try {
            val file = File(FILE_CONFIG)
            if (!file.exists() && file.createNewFile()) {
                logger.info("Файл с конфигурацией приложения создан!")
            }

            val import = gson.fromJson(FileReader(file), ConfigModel::class.java)

            attemptRequest = import.attemptRequest
            isEnabledTray = import.isEnabledTray
        } catch (ignored: Exception) {
            logger.error("Не удалось инициализировать конфигурацию. Загружен стандартный файл")
        }
    }

    fun save() {
        FileWriter(FILE_CONFIG, StandardCharsets.UTF_8).use {
            it.write(gson.toJson(this))
            it.flush()
        }
    }

}
