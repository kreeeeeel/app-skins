package com.project.app.service.mafile

import com.project.app.models.ImportAccount
import com.project.app.models.PasswordFile
import java.io.File

interface ImportFile {

    fun import(files: List<File>): ImportAccount
    fun getPassword(file: File, importAccount: ImportAccount): PasswordFile

}