package com.project.app.handler

import com.google.gson.Gson
import com.project.app.property.SteamProperty
import java.io.File
import java.io.FileReader

private const val SIZE_FILE = 1048576

class MaFileHandler {

    fun getSteamProperty(file: File): SteamProperty? {

        if (!file.name.endsWith(".maFile") || file.length() > SIZE_FILE) {
            return null
        }

        try {
            FileReader(file).use { reader ->
                return Gson().fromJson(reader, SteamProperty::class.java)
            }
        } catch (e: Exception) {
            return null
        }
    }

}