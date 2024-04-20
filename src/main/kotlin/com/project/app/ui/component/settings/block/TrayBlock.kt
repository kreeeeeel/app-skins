package com.project.app.ui.component.settings.block

import com.project.app.models.ConfigModel
import com.project.app.ui.component.settings.SettingBlock
import javafx.scene.control.Button
import javafx.scene.control.Label

class TrayBlock(config: ConfigModel): SettingBlock("Трей", "system", config) {

    private val hint = Label("Выберите действие, которое выполнять при закрытии приложения").also {
        it.id = "trayHint"
        it.layoutX = 21.0
        it.layoutY = 49.0
    }

    private val value = Label().also {
        it.id = "trayValue"
        it.layoutX = 50.0
        it.layoutY = 106.0
    }

    private val valueText = Label("Текущее значение").also {
        it.id = "trayValueText"
        it.layoutX = 82.0
        it.layoutY = 127.0
    }

    private val inTray = Button("Сворачивать в Tray").also {
        it.id = "btnInTray"
        it.layoutX = 30.0
        it.layoutY = 168.0

        it.setOnMouseClicked {
            config.isEnabledTray = true
            config.save()

            redrawing()
        }
    }

    private val closeApp = Button("Закрыть приложение").also {
        it.id = "btnCloseApp"
        it.layoutX = 30.0
        it.layoutY = 211.0

        it.setOnMouseClicked {
            config.isEnabledTray = false
            config.save()

            redrawing()
        }
    }

    private val alwaysQuestion = Label("Всегда спрашивать действие").also {
        it.id = "alwaysQuestion"
        it.layoutX = 50.0
        it.layoutY = 258.0

        it.setOnMouseClicked {
            config.isEnabledTray = null
            config.save()

            redrawing()
        }
    }

    override fun init(): Double {
        block.children.addAll(hint, value, valueText, inTray, closeApp, alwaysQuestion)
        block.prefHeight = 290.0

        redrawing()
        return block.prefHeight
    }

    private fun redrawing() {
        value.text = (if (config.isEnabledTray == null) "Всегда спрашивать"
        else (if (config.isEnabledTray == true) "Сворачивать в Tray" else "Закрывать приложение"))

        value.layoutX = if (config.isEnabledTray == false) 30.0 else 50.0

        inTray.isDisable = config.isEnabledTray == true
        closeApp.isDisable = config.isEnabledTray == false
        alwaysQuestion.isDisable = config.isEnabledTray == null
    }

}