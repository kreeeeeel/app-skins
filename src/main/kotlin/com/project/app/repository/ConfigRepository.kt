package com.project.app.repository

import com.google.gson.GsonBuilder
import com.project.app.property.ConfigProperty
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets

private const val FILE_CONFIG = "config.json"

class ConfigRepository {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun save(configProperty: ConfigProperty) {
        FileWriter(FILE_CONFIG, StandardCharsets.UTF_8).use {
            it.write(gson.toJson(configProperty))
            it.flush()
        }
    }

    fun find(): ConfigProperty {
        val file = File(FILE_CONFIG)

        if (!file.exists()) {
            return ConfigProperty()
        }

        return gson.fromJson(FileReader(file), ConfigProperty::class.java)
    }

}