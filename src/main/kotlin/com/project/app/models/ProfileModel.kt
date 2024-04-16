package com.project.app.models

import java.math.BigDecimal

data class ProfileModel(
    val username: String,
    val name: String,
    val photo: String,
    val password: String,
    val inventory: BigDecimal = BigDecimal.ZERO
)
