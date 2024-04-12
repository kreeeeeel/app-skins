package com.project.app.models

data class PasswordFile(
    val users: Map<String, ValidProperty>,
    val invalid: List<String>
)
