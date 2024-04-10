package com.project.app.ui.component

import com.project.app.ui.controller.HEIGHT
import com.project.app.ui.controller.WIDTH
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

class MessageComponent(
    private val root: Pane
) {

    private val pane = Pane().also {
        it.id = "background"
        it.prefWidth = WIDTH
        it.prefHeight = HEIGHT
    }

    private val icon = ImageView().also {
        it.fitWidth = 96.0
        it.fitHeight = 96.0
        it.layoutX = 552.0
        it.layoutY = 141.0
    }

    private val title = Label().also {
        it.id = "message-title"
        it.layoutX = 470.0
        it.layoutY = 304.0
    }

    private val description = Label().also {
        it.id = "message-description"
        it.layoutY = 338.0
    }

    private val button = Button("Закрыть окно").also {
        it.layoutX = 490.0
        it.layoutY = 443.0
    }

    fun drawSuccessMessage(desc: String) = init(true, desc)
    fun drawErrorMessage(desc: String) = init(false, desc)

    private fun init(isSuccess: Boolean, desc: String) = Platform.runLater {
        icon.id = if (isSuccess) "success" else "error"
        title.text = if (isSuccess) "Успешно!" else "Произошла ошибка!"
        description.text = desc

        button.setOnMouseClicked { root.children.remove(pane) }

        pane.children.addAll(icon, title, description, button)
        root.children.add(pane)
    }

}