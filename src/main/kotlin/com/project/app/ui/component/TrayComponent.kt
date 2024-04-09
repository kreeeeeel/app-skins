package com.project.app.ui.component

import com.project.app.Desktop
import com.project.app.repository.ConfigRepository
import com.project.app.ui.controller.ICON_TRAY
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.awt.AWTException
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import javax.imageio.ImageIO
import kotlin.system.exitProcess

class TrayComponent: BaseComponent() {

    private val block = Pane().also {
        it.id = "block-tray"
        it.layoutX = 375.0
        it.layoutY = 252.0

        val logo = ImageView().also { img ->
            img.id = "system"
            img.layoutX = 14.0
            img.layoutY = 6.0
            img.fitWidth = 36.0
            img.fitHeight = 36.0
        }

        val title = Label("Выход из приложения").also { l ->
            l.id = "title-ma-file"
            l.layoutX = 55.0
            l.layoutY = 14.0
        }

        val description = Label("Вы действительно хотите закрыть приложение, или же продолжить его работу свернув его в tray?").also { l ->
            l.id = "tray-text"
            l.layoutX = 39.0
            l.layoutY = 62.0
        }

        it.children.addAll(logo, title, description)
    }

    private val collapse = Button("Свернуть").also {
        it.id = "trayButton"
        it.layoutX = 62.0
        it.layoutY = 112.0

        block.children.add(it)
    }

    private val close = Button("Закрыть").also {
        it.id = "closeButton"
        it.layoutX = 226.0
        it.layoutY = 112.0

        block.children.add(it)
    }

    private val remember = CheckBox("Запомнить действие?").also {
        it.id = "remember-interactive"
        it.layoutX = 18.0
        it.layoutY = 164.0

        block.children.add(it)
    }

    private val configRepository = ConfigRepository()
    private val configProperty = configRepository.find()

    override fun init(root: Pane) {

        close.setOnMouseClicked { actionOnButton(root, true) }
        collapse.setOnMouseClicked { actionOnButton(root, false) }

        if (configProperty.isTrayEnabled != null) inTrayOrClose(root.scene.window as Stage)
        else {
            pane.children.add(block)
            super.init(root)
        }

    }

    private fun actionOnButton(root: Pane, isClose: Boolean) {
        root.children.remove(pane)

        configProperty.isTrayEnabled = !isClose
        if (remember.isSelected) {
            configRepository.save(configProperty)
        }
        inTrayOrClose(root.scene.window as Stage)
    }

    private fun inTrayOrClose(stage: Stage) = Platform.runLater {

        if (SystemTray.isSupported() && configProperty.isTrayEnabled!!) {

            stage.hide()

            val tray = SystemTray.getSystemTray()
            val url = Desktop::class.java.getResource(ICON_TRAY)

            val image = ImageIO.read(url)

            val trayIcon = TrayIcon(image, "Менеджер предметов")
            trayIcon.isImageAutoSize = true

            try {
                tray.add(trayIcon)
            } catch (e: AWTException) {
                e.printStackTrace()
            }

        } else exitProcess(0)
    }

}