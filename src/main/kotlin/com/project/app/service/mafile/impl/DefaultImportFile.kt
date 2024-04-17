package com.project.app.service.mafile.impl

import com.google.gson.GsonBuilder
import com.project.app.data.MaFileData
import com.project.app.data.PasswordFile
import com.project.app.data.ValidData
import com.project.app.data.SteamProperty
import com.project.app.repository.MaFileRepository
import com.project.app.service.logger.Logger
import com.project.app.service.logger.impl.DefaultLogger
import com.project.app.service.mafile.ImportFile
import java.io.File
import java.io.FileReader

class DefaultImportFile: ImportFile {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val maFileRepository = MaFileRepository()
    private val logger: Logger = DefaultLogger()

    override fun import(files: List<File>): MaFileData {

        logger.info("Получение информации с ${files.size} файлов с расширением .maFile")

        val names = mutableSetOf<String>()

        val invalid = mutableListOf<File>()
        val properties = mutableListOf<ValidData>()

        for (file in files) {

            val property = getPropertyFile(file)
            if (property != null && !names.contains(property.accountName)) {

                names.add(property.accountName)
                val validData = ValidData(
                    username = property.accountName.lowercase(),
                    sharedSecret = property.sharedSecret,
                    identitySecret = property.identitySecret,
                    file = file
                )
                properties.add(validData)
                logger.info("Файл ${file.name} валиден, полученный аккаунт ${validData.username}.")
                maFileRepository.save(property)

            } else {

                logger.error("Файл ${file.name} невалиден")
                invalid.add(file)
            }

        }

        return MaFileData(size = invalid.size + properties.size, invalid = invalid, data = properties)
    }

    override fun getPassword(file: File, maFileData: MaFileData): PasswordFile {

        val users = maFileData.data.associateBy { it.username.lowercase() }.toMutableMap()
        users.values.forEach{ it.password = null }

        FileReader(file).use { reader ->
            reader.readLines().forEach {
                val split = it.split(":")
                if (split.size == 2 && users[split[0].lowercase()]?.password == null) {
                    users[split[0].lowercase()]?.password = split[1]
                }
            }
        }

        return PasswordFile(
            users.filter { it.value.password != null },
            users.filter { it.value.password == null }.map { it.key }
        )
    }

    private fun getPropertyFile(file: File): SteamProperty? {
        return try {
            gson.fromJson(file.reader(), SteamProperty::class.java)
        }
        catch (e: Exception) { null }
    }


}