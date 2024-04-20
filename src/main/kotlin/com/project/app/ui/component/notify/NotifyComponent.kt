package com.project.app.ui.component.notify

import com.project.app.ui.controller.BaseController.Companion.root
import javafx.animation.PauseTransition
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.util.Duration

class NotifyComponent {

    private val pane = Pane().also {
        it.layoutX = 330.0
    }

    private val icon = ImageView().also {
        it.fitWidth = 36.0
        it.fitHeight = 36.0
        it.layoutY = 17.0
        it.layoutX = 14.0

        pane.children.add(it)
    }

    private val title = Label().also {
        it.id = "notifyTitle"
        it.layoutX = 58.0
        it.layoutY = 6.0

        pane.children.add(it)
    }

    private val description = Label().also {
        it.id = "notifyDesc"
        it.layoutX = 58.0
        it.layoutY = 26.0

        pane.children.add(it)
    }

    fun success(value: String) = show(true, value)
    fun failure(value: String) = show(false, value)

    private fun show(isSuccess: Boolean, value: String) {

        Platform.runLater {
            root.children.add(pane)
            pane.id = if (isSuccess) "notifySuccess" else "notifyFailure"
            icon.id = if (isSuccess) "success" else "error"

            title.text = if (isSuccess) "Успешный успех!" else "Произошла ошибочка!"
            description.text = value

            val firstAnim = TranslateTransition(Duration.millis(430.0), pane)
            firstAnim.fromY = -200.0
            firstAnim.toY = 35.0
            firstAnim.cycleCount = 1
            firstAnim.isAutoReverse = true

            val secondAnim = TranslateTransition(Duration.millis(430.0), pane)
            secondAnim.toY = -200.0
            secondAnim.cycleCount = 1
            secondAnim.isAutoReverse = true

            firstAnim.setOnFinished {
                val pause = PauseTransition(Duration.seconds(5.0))
                pause.setOnFinished {
                    secondAnim.playFromStart()
                }
                pause.play()
            }

            firstAnim.playFromStart()

        }
    }

}