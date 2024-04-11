package com.project.app.service.models

import java.security.spec.RSAPublicKeySpec

data class RSAParam(
    val pubKeySpecval : RSAPublicKeySpec,
    val timestamp: Long
)
