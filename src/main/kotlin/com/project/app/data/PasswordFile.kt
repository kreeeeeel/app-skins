package com.project.app.data

data class PasswordFile(
    val users: Map<String, ValidData>,
    val invalid: List<String>
)
