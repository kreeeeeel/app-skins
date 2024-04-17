package com.project.app.ui.component.message

import javafx.animation.FadeTransition
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.util.Duration

class CenterComponent(
    private val text: String
) {

    private val pane = Pane().also {
        it.id = "centerMessage"
        it.layoutX = 430.0
        it.layoutY = 308.0

        Label(text).also{ l ->
            l.id = "centerMessageText"
            l.layoutX = 9.0
            l.layoutY = 11.0

            it.children.add(l)
        }
    }

    fun show(root: Pane) {
        root.children.add(pane)
        FadeTransition(Duration(1350.0), pane).also {

            it.fromValue = 0.0
            it.byValue = 1.0
            it.isAutoReverse = true
            it.setOnFinished {

                FadeTransition(Duration(1350.0), pane).also { delete ->
                    delete.fromValue = 1.0
                    delete.toValue = 0.0
                    delete.isAutoReverse = true
                    delete.setOnFinished {
                        root.children.remove(pane)
                    }
                }.playFromStart()

            }

        }.playFromStart()
    }

}