package com.project.app.service.mafile

import com.project.app.data.MaFileData
import com.project.app.data.PasswordFile
import java.io.File

interface ImportFile {

    fun import(files: List<File>): MaFileData
    fun getPassword(file: File, maFileData: MaFileData): PasswordFile

}