package com.project.app

import com.project.app.ui.controller.AccountController
import javafx.application.Application

const val TITLE = "Steam Farm"
const val DESCRIPTION = "Взаимодействие с Steam"
const val VERSION = "1.0.0"

class Desktop

fun main() {
    Application.launch(AccountController::class.java)
}