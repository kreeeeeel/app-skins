package com.project.app.ui.component.auth

import com.project.app.data.PasswordFile
import com.project.app.ui.component.BaseComponent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import kotlin.math.max

class InvalidPasswordComponent(
    private val passwordFile: PasswordFile
): BaseComponent() {

    private val block = Pane().also {
        it.id = "invalidPassword"
        it.layoutX = 450.0
        it.layoutY = 131.0

        val logo = ImageView().also { img ->
            img.id = "error"
            img.layoutX = 14.0
            img.layoutY = 14.0
            img.fitWidth = 48.0
            img.fitHeight = 48.0
        }

        val title = Label("Не найдено ${passwordFile.invalid.size} паролей").also { l ->
            l.id = "titleInvalidPass"
            l.layoutX = 68.0
            l.layoutY = 20.0
        }

        val description = Label("Дальнейшие действия?").also { l ->
            l.id = "descInvalidPass"
            l.layoutX = 68.0
            l.layoutY = 38.0
        }

        val hint = Label("Для продолжения авторизации аккаунтов для которых пароль был найден, нажмите 'Начать'").also { l ->
            l.id = "hintInvalidPass"
            l.layoutX = 25.0
            l.layoutY = 323.0
        }

        val button = Button("Начать").also { b ->
            b.layoutX = 40.0
            b.layoutY = 384.0
            b.setOnMouseClicked {
                val valid = passwordFile.users
                    .filter { f -> !passwordFile.invalid.contains(f.value.username) }
                    .map { f -> f.value }
                    .toList()

                SteamComponent(valid).start(pane.parent as Pane)
            }
        }

        it.children.addAll(logo, title, description, hint, button)
        pane.children.addAll(it)
    }

    private val field = Pane().also {
        it.id = "fieldInvalidPass"
        it.layoutX = 25.0
        it.layoutY = 77.0

        block.children.addAll(it)
    }

    private val scroll = ScrollPane().also {
        it.prefWidth = 250.0
        it.prefHeight = 235.0
        it.content = AnchorPane().also { ap ->
            ap.prefWidth = 235.0
            ap.prefHeight = 233.0
        }

        field.children.add(it)
    }

    override fun init(root: Pane) {
        initScroll()
        super.init(root)
    }

    private fun initScroll() {
        val content = scroll.content as AnchorPane
        var count = 0

        passwordFile.invalid.forEach {
            val pane = getPane(it).also { p -> p.layoutY = 14.0 + (50*count++) }
            content.children.add(pane)
        }
        content.prefHeight = max(content.prefHeight, 14.0 + (50*count))
    }

    private fun getPane(username: String) = Pane().also {
        it.id = "dataInvalidPass"
        it.layoutX = 14.0

        val icon = ImageView().also { img ->
            img.id = "question"
            img.layoutX = 8.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
            img.layoutY = 8.0
        }

        val text = Label(username).also { l ->
            l.id = "textForData"
            l.layoutX = 40.0
            l.layoutY = 10.0
        }

        it.children.addAll(icon, text)
    }

}