package com.project.app.ui.component

import com.project.app.handler.DriverHandler
import com.project.app.property.SteamProperty
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import java.util.concurrent.CompletableFuture

private const val HINT_TEXT = "Пароль как и все остальные данные будет храниться только на ВАШЕМ компьютере, в зашифрованном виде. После того как вы укажите пароль, приложение проверит что аккаунт является валидным"

class PasswordComponent(
    private val steamProperty: SteamProperty
): BaseComponent() {

    private val block = Pane().also {
        it.id = "block-ma-file"
        it.layoutX = 450.0
        it.layoutY = 160.0

        val logo = ImageView().also { img ->
            img.id = "lock"
            img.layoutX = 27.0
            img.layoutY = 16.0
            img.fitWidth = 36.0
            img.fitHeight = 36.0
        }

        val title = Label("Добавление аккаунта").also { l ->
            l.id = "title-ma-file"
            l.layoutX = 80.0
            l.layoutY = 14.0
        }

        val description = Label("Авторизация через Steam").also { l ->
            l.id = "description-ma-file"
            l.layoutX = 80.0
            l.layoutY = 34.0
        }


        val hint = Label(HINT_TEXT).also { l ->
            l.id = "hint-ma-file"
            l.layoutX = 25.0
            l.layoutY = 196.0
        }

        val hint2 = Label("Укажите пароль для аккаунта").also { l ->
            l.id = "hint-ma-file-2"
            l.layoutX = 60.0
            l.layoutY = 116.0
        }

        it.children.addAll(logo, title, description, hint, hint2)
    }

    private val username = Label(steamProperty.accountName).also {
        it.id = "username-ma-file"
        it.layoutY = 92.0
    }

    private val textField = TextField().also {
        it.isFocusTraversable = false
        it.layoutX = 29.0
        it.layoutY = 145.0
        it.promptText = "Пароль"
    }

    private val button = Button("Авторизоваться").also {
        it.layoutX = 40.0
        it.layoutY = 320.0
    }

    override fun init(root: Pane) = Platform.runLater {
        root.children.removeIf { it.id == "background" }
        block.children.addAll(username, textField, button)
        pane.children.add(block)

        button.setOnMouseClicked { callback(root) }
        super.init(root)
    }

    private fun callback(root: Pane) {

        val loadingComponent = LoadingComponent(root).also { it.initialize() }
        val messageComponent = MessageComponent(root)
        val accountComponent = AccountComponent(root)

        var isSuccessAuth = false
        val future = CompletableFuture.runAsync {
            isSuccessAuth = DriverHandler().auth(steamProperty, textField.text.trim())
        }

        future.thenRun {
            Platform.runLater {
                loadingComponent.clear()

                if (!isSuccessAuth) {
                    messageComponent.drawErrorMessage("Возможно вы указали неверный пароль, попробуйте еще раз..")

                } else {
                    accountComponent.initializeOrUpdate()
                    messageComponent.drawSuccessMessage("Аккаунт был успешно добавлен!")
                }
            }
        }
    }

}