package com.project.app.repository

import com.google.gson.GsonBuilder
import com.project.app.property.ProfileProperty
import java.io.File
import java.io.FileWriter
import java.nio.charset.StandardCharsets

private const val PATH_TO_PROFILES = "/profiles"

class ProfileRepository() {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun save(profileProperty: ProfileProperty) {
        val path = String.format("%s\\%s\\%s",
            System.getProperty("user.dir"), PATH_TO_PROFILES, profileProperty.steam!!.session.steamID
        )

        val directory = File(path)
        directory.mkdirs()

        FileWriter("$path\\profile.json", StandardCharsets.UTF_8).use {
            it.write(gson.toJson(profileProperty))
            it.flush()
        }
    }

    fun findAll(): List<ProfileProperty> {

        val files = File("${System.getProperty("user.dir")}\\$PATH_TO_PROFILES").listFiles()
        if (files == null || files.isEmpty()) {
            return listOf()
        }

        return files.map {
            val file = File("${it.absolutePath}\\profile.json")
            return@map gson.fromJson(file.readText(), ProfileProperty::class.java)
        }

    }

    fun remove(profileProperty: ProfileProperty): Boolean {
        val path = String.format("%s\\%s\\%s",
            System.getProperty("user.dir"), PATH_TO_PROFILES, profileProperty.steam!!.session.steamID
        )

        val file = File(path)
        return file.deleteRecursively()
    }

}