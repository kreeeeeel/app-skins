package com.project.app.ui.component.message

import com.project.app.ui.controller.HEIGHT
import com.project.app.ui.controller.WIDTH
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import com.project.app.ui.controller.BaseController.Companion.root

class LoadingComponent {

    private val pane = Pane().also{
        it.id = "background"
        it.prefWidth = WIDTH
        it.prefHeight = HEIGHT

        val icon = ImageView().also { img ->
            img.id = "loading"
            img.layoutX = 315.0
            img.layoutY = 135.0
            img.fitWidth = 560.0
            img.fitHeight = 312.0
        }

        val text = Label("Это может занять некоторое время...").also { l ->
            l.id = "loading-text"
            l.layoutX = 435.0
            l.layoutY = 373.0
        }

        it.children.addAll(icon, text)
    }

    fun initialize() = Platform.runLater{ root.children.add(pane) }
    fun clear() = Platform.runLater { root.children.remove(pane) }
}