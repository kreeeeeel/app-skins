package com.project.app.ui.component

import com.project.app.ui.controller.BaseController.Companion.getFooterRight
import com.project.app.ui.controller.HEIGHT
import com.project.app.ui.controller.WIDTH
import javafx.animation.FadeTransition
import javafx.application.Platform
import javafx.scene.layout.Pane
import javafx.util.Duration

open class BaseComponent {

    val pane = Pane().also {
        it.id = "background"
        it.prefWidth = WIDTH
        it.prefHeight = HEIGHT
    }

    private val close = getFooterRight("close").also {
        it.layoutX = 1110.0
        it.layoutY = 42.0

        pane.children.add(it)
    }

    open fun init(root: Pane) = Platform.runLater {
        root.children.add(pane)
        close.setOnMouseClicked { root.children.remove(pane) }
    }

    fun animate() = Platform.runLater {
        FadeTransition(Duration(230.0), pane).also {
            it.fromValue = 0.0
            it.byValue = 1.0
            it.isAutoReverse = true
        }.playFromStart()
    }

}