package com.project.app.repository

import com.project.app.service.logger.Logger
import com.project.app.service.logger.impl.DefaultLogger
import javafx.scene.image.Image
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

private val PATH_TO_AVATARS = System.getProperty("user.dir") + "/avatars"

class AvatarRepository {

    private val profileRepository = ProfileRepository()
    private val logger: Logger = DefaultLogger()

    fun getAvatar(username: String): Image? {
        val directory = File(PATH_TO_AVATARS)
        if (!directory.exists() && directory.mkdir()) {
            logger.info("Создание директории с аватарками пользователей")
        }

        logger.info("Поиск фото профиля: $username")
        val files = directory.listFiles()
        if (files == null || files.isEmpty()) {
            return downloadAvatar(username)
        }

        val file = files.firstOrNull { it.name.startsWith(username) }
        if (file == null){
            return downloadAvatar(username)
        }

        logger.info("Фото профиля: $username найдено, путь: ${file.absolutePath}")
        return Image(file.toURI().toString())
    }

    private fun downloadAvatar(username: String): Image? {
        logger.info("Фото профиля: $username не найдено, скачивание..")
        val profile = profileRepository.find(username) ?: return null

        val link = profile.photo
        val prefix = link.substring(link.lastIndexOf('.') + 1, link.length)

        val bytes = getBytesFromUrl(link)
        val inputStream = ByteArrayInputStream(bytes)

        val result = Image(inputStream)

        val path = String.format("%s/%s.%s", PATH_TO_AVATARS, username, prefix)
        val file = File(path).also { it.createNewFile() }

        FileOutputStream(file).use { outputStream -> outputStream.write(bytes) }
        logger.info("Фото профиля: $username скачано!")
        return result
    }

    private fun getBytesFromUrl(url: String): ByteArray {
        val outputStream = ByteArrayOutputStream()
        URL(url).openStream().use { inputStream ->
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
        }
        return outputStream.toByteArray()
    }

}