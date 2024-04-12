package com.project.app.models

import java.io.File

data class ImportAccount(
    val size: Int,
    val badFiles: List<File>,
    val properties: List<ValidProperty>
)

data class ValidProperty(
    val username: String,
    val sharedSecret: String,
    val identitySecret: String,
    var password: String? = null,
    val file: File
)
