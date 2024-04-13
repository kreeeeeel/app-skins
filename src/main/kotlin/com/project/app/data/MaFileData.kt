package com.project.app.data

import java.io.File

data class MaFileData(
    val size: Int,
    val invalid: List<File>,
    val data: List<ValidData>
)

data class ValidData(
    val username: String,
    val sharedSecret: String,
    val identitySecret: String,
    var password: String? = null,
    val file: File
)
