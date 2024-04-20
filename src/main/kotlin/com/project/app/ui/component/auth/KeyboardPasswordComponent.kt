package com.project.app.ui.component.auth

import com.project.app.data.ValidData
import com.project.app.ui.component.BaseComponent
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.util.Duration

private const val HINT_TEXT = "Пароль как и все остальные данные будет храниться только на ВАШЕМ компьютере, в зашифрованном виде. После того как вы укажите пароль, приложение проверит что аккаунт является валидным"

class KeyboardPasswordComponent(
    private val validData: List<ValidData>,
): BaseComponent() {

    private var current = 0

    private val upper = Pane().also {
        it.id = "keyboardPass"
        it.layoutX = 450.0
        it.layoutY = 100.0

        val icon = ImageView().also { img ->
            img.id = "question"
            img.layoutX = 14.0
            img.layoutY = 16.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        val title = Label(getTitle()).also { l ->
            l.id = "keyboardPassTitle"
            l.layoutX = 51.0
            l.layoutY = 10.0
        }

        val desc = Label(getDesc()).also { l ->
            l.id = "keyboardPassDesc"
            l.layoutX = 51.0
            l.layoutY = 28.0
        }

        it.children.addAll(icon, title, desc)
        pane.children.add(it)
    }

    private val block = Pane().also {
        it.id = "block-ma-file"
        it.layoutX = 450.0
        it.layoutY = 180.0

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

        val description = Label("Установка пароля").also { l ->
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

    private val username = Label(validData[current].username).also {
        it.id = "username-ma-file"
        it.layoutY = 92.0
    }

    private val textField = TextField().also {
        it.isFocusTraversable = false
        it.layoutX = 29.0
        it.layoutY = 145.0
        it.promptText = "Пароль"

        it.textProperty().addListener { _, _, newValue ->
            button.isDisable = newValue.isEmpty()
        }

        it.setOnKeyReleased { event ->
            if (event.code == KeyCode.ENTER) {
                savePassword()
            }
        }
    }

    private val button = Button("Ввести").also {
        it.layoutX = 40.0
        it.layoutY = 320.0
        it.isDisable = true

        it.setOnMouseClicked { savePassword() }
    }

    private val left = Pane().also {

        it.id = "arrow"
        it.isDisable = true
        it.layoutX = 175.0
        it.layoutY = 305.0

        val icon = ImageView().also { img ->
            img.opacity = 0.5
            img.id = "left"
            img.fitWidth = 90.0
            img.fitHeight = 90.0
        }

        it.setOnMouseClicked { arrowClick(false) }
        it.children.addAll(icon)
        pane.children.add(it)
    }

    private val right = Pane().also {

        it.id = "arrow"
        it.isDisable = true
        it.layoutX = 950.0
        it.layoutY = 305.0

        val icon = ImageView().also { img ->
            img.opacity = 0.5
            img.id = "right"
            img.fitWidth = 90.0
            img.fitHeight = 90.0
        }

        it.setOnMouseClicked { arrowClick(true) }

        it.children.addAll(icon)
        pane.children.add(it)
    }

    override fun init() = Platform.runLater {
        block.children.addAll(username, textField, button)
        pane.children.add(block)

        super.init()
    }

    private fun savePassword() {

        val password = textField.text.trim()
        if (password.isNotEmpty()) {

            validData[current++].password = password
            if (current == validData.size) {
                SteamComponent(validData).start()
            } else {
                refreshUi()
            }

        }

    }

    private fun arrowClick(isNext: Boolean) {
        current = if (isNext) current.plus(1) else current.minus(1)
        refreshUi()
    }

    private fun refreshUi() {
        Platform.runLater {
            val title = upper.children.first { it.id == "keyboardPassTitle" } as Label
            val desc = upper.children.first { it.id == "keyboardPassDesc" } as Label

            val leftIcon = left.children.first { it.id == "left" } as ImageView
            val rightIcon = right.children.first { it.id == "right" } as ImageView

            left.isDisable = current - 1 < 0
            right.isDisable = validData[current].password == null

            leftIcon.opacity = if (left.isDisable) 0.5 else 1.0
            rightIcon.opacity = if (right.isDisable) 0.5 else 1.0

            title.text = getTitle()
            desc.text = getDesc()

            username.text = validData[current].username
            if (validData[current].password != null) {
                textField.text = validData[current].password
                textField.positionCaret(textField.text.length)
            } else {
                textField.clear()
            }

            val transition = TranslateTransition(Duration(40.0), block)

            transition.cycleCount = 4
            transition.isAutoReverse = true

            transition.byX = 10.0
            transition.byY = 0.0

            transition.play()
        }
    }

    private fun getTitle(): String = "Ручной ввод ${current + 1} из ${validData.size}"

    private fun getDesc(): String =
        if (current == validData.size - 1) "Последний ввод" else "Осталось ${validData.size - current}"


}