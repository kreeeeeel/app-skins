package com.project.app.service.mafile.impl

import com.google.gson.GsonBuilder
import com.project.app.models.ImportAccount
import com.project.app.models.PasswordFile
import com.project.app.models.ValidProperty
import com.project.app.property.SteamProperty
import com.project.app.service.mafile.ImportFile
import java.io.File
import java.io.FileReader

class DefaultImportFile: ImportFile {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun import(files: List<File>): ImportAccount {

        val names = mutableSetOf<String>()

        val invalid = mutableListOf<File>()
        val properties = mutableListOf<ValidProperty>()

        for (file in files) {

            val property = getPropertyFile(file)
            if (property != null && !names.contains(property.accountName)) {

                names.add(property.accountName)
                val validProperty = ValidProperty(
                    username = property.accountName.lowercase(),
                    sharedSecret = property.sharedSecret,
                    identitySecret = property.identitySecret,
                    file = file
                )
                properties.add(validProperty)

            } else invalid.add(file)

        }

        return ImportAccount(size = invalid.size + properties.size, badFiles = invalid, properties = properties)
    }

    override fun getPassword(file: File, importAccount: ImportAccount): PasswordFile {
        val users = importAccount.properties.associateBy { it.username.lowercase() }.toMutableMap()
        FileReader(file).use { reader ->
            reader.readLines().forEach {
                val split = it.split(":")
                if (split.size == 2 && users[split[0].lowercase()]?.password != null) {
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