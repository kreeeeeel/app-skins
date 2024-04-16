package com.project.app

import com.project.app.service.logger.Logger
import com.project.app.service.logger.impl.DefaultLogger
import com.project.app.ui.controller.AccountController
import javafx.application.Application

const val TITLE = "Steam Farm"
const val DESCRIPTION = "Взаимодействие с Steam"

private val logger: Logger = DefaultLogger()

class Desktop

fun main() {
    logger.info("Запуск приложения")
    Application.launch(AccountController::class.java)
}