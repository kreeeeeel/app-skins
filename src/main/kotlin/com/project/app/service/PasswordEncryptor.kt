package com.project.app.service

import java.security.spec.RSAPublicKeySpec

interface PasswordEncryptor {
    fun encrypt(pubKeySpec: RSAPublicKeySpec, pass: String): String?
}