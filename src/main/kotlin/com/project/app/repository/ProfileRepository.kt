package com.project.app.repository

import com.google.gson.GsonBuilder
import com.project.app.models.ProfileModel
import java.io.File
import java.io.FileReader
import java.io.FileWriter

private const val PROFILE_PATH = "/profile"
private const val PROFILE_INFO = "/profile.json"

class ProfileRepository {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun save(model: ProfileModel): Boolean {
        val path = String.format("%s/%s/%s", System.getProperty("user.dir"), PROFILE_PATH, model.username)

        val directory = File(path)
        if (!directory.exists() && !directory.mkdirs()) {
            return false
        }

        val file = File(String.format("%s%s", path, PROFILE_INFO))
        FileWriter(file).use { writer ->
            writer.write(gson.toJson(model))
            writer.flush()
        }

        return true
    }

    fun findAll(): List<ProfileModel> {

        val path = String.format("%s/%s", System.getProperty("user.dir"), PROFILE_PATH)

        val files = File(path).listFiles()
        if (files == null || files.isEmpty()) {
            return listOf()
        }

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

        val file = File(path)
        return file.deleteRecursively()
    }

}