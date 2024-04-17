package com.project.app.repository

import com.google.gson.GsonBuilder
import com.project.app.data.SteamProperty
import com.project.app.service.logger.Logger
import com.project.app.service.logger.impl.DefaultLogger
import java.io.File
import java.io.FileReader
import java.io.FileWriter

private val PATH_TO_MA_FILE = System.getProperty("user.dir") + "/mafiles"

class MaFileRepository {

    private val logger: Logger = DefaultLogger()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun find(username: String): SteamProperty? {

        logger.info("Поиск .maFile аккаунта: $username")

        val path = String.format("%s/%s.maFile", PATH_TO_MA_FILE, username)
        val file = File(path)
        if (!file.exists()) {
            logger.error(".maFile Файл с данными: $username небыл найден!")
            return null
        }

        logger.info(".maFile аккаунта $username найден, получение информации!")
        return gson.fromJson(FileReader(file).use { reader -> reader.readText() }, SteamProperty::class.java)
    }

    fun save(steamProperty: SteamProperty) {

        logger.info("Попытка сохранения .maFile аккаунта: ${steamProperty.accountName}")
        val path = String.format("%s/%s.maFile", PATH_TO_MA_FILE, steamProperty.accountName)

        val directory = File(PATH_TO_MA_FILE)
        if (!directory.exists() && directory.mkdirs()) {
            logger.info("Создание хранилища с данными .maFile")
        }

        val file = File(path)
        FileWriter(file).use { writer ->
            writer.write(gson.toJson(steamProperty))
            writer.flush()
        }

        logger.info(".maFile аккаунта: ${steamProperty.accountName} был успешно сохранен!")
    }

}