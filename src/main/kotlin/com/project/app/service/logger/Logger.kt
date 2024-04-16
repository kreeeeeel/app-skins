package com.project.app.service.logger

interface Logger {
    fun debug(message: String)
    fun info(message: String)
    fun warning(message: String)
    fun error(message: String)
}