package com.project.app.models

import java.math.BigDecimal

data class ProfileModel(
    var username: String,
    var name: String,
    var photo: String,
    var password: String,
    var inventory: BigDecimal = BigDecimal.ZERO
)
