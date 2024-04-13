package com.project.app.data

import java.security.spec.RSAPublicKeySpec

data class RSAParam(
    val pubKeySpecval : RSAPublicKeySpec,
    val timestamp: Long
)
