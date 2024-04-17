package com.project.app.repository

import com.google.gson.GsonBuilder
import com.project.app.models.ProfileModel
import com.project.app.service.logger.Logger
import com.project.app.service.logger.impl.DefaultLogger
import java.io.File
import java.io.FileReader
import java.io.FileWriter

private const val PROFILE_PATH = "/profile"
private const val PROFILE_INFO = "/profile.json"

class ProfileRepository {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val logger: Logger = DefaultLogger()

    fun save(model: ProfileModel): Boolean {

        logger.info("Попытка сохранения аккаунта: ${model.username}")
        val path = String.format("%s/%s/%s", System.getProperty("user.dir"), PROFILE_PATH, model.username)

        val directory = File(path)
        if (!directory.exists() && !directory.mkdirs()) {
            logger.error("Не удалось сохранить аккаунт: ${model.username}")
            return false
        }

        val file = File(String.format("%s%s", path, PROFILE_INFO))
        FileWriter(file).use { writer ->
            writer.write(gson.toJson(model))
            writer.flush()
        }

        logger.info("Аккаунт: ${model.username} был успешно сохранен!")
        return true
    }

    fun findAll(): List<ProfileModel> {

        logger.info("Поиск всех доступных аккаунтов..")
        val path = String.format("%s/%s", System.getProperty("user.dir"), PROFILE_PATH)

        val files = File(path).listFiles()
        if (files == null || files.isEmpty()) {
            logger.info("Доступные аккаунты небыли найдены!")
            return listOf()
        }

        logger.info("Получено ${files.size} доступных аккаунтов!")
        return files.map {
            FileReader( String.format("%s/%s", it.absolutePath, PROFILE_INFO) ).use { reader ->
                gson.fromJson(reader, ProfileModel::class.java)
            }
        }

    }

    fun remove(profileModel: ProfileModel): Boolean {
        val path = String.format("%s/%s/%s",
            System.getProperty("user.dir"), PROFILE_PATH, profileModel.username
        )

        logger.info("Удаление аккаунта: ${profileModel.username}")
        val file = File(path)
        return file.deleteRecursively()
    }

    fun find(username: String): ProfileModel? {

        logger.info("Поиск информации об аккаунте: $username")

        val path = String.format("%s/%s/%s/%s", System.getProperty("user.dir"), PROFILE_PATH, username, PROFILE_INFO)
        val file = File(path)
        if (!file.exists()) {
            logger.error("Аккаунт: $username небыл найден!")
            return null
        }

        logger.info("Аккаунт: $username найдена, получение информации!")
        return gson.fromJson(FileReader(file).use { reader -> reader.readText() }, ProfileModel::class.java)
    }

}