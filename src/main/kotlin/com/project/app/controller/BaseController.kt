package com.project.app.controller

import com.project.app.Desktop
import com.sun.javafx.scene.control.skin.Utils.getResource
import javafx.application.Application
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File

private const val WIDTH = 1200.0
private const val HEIGHT = 700.0

class BaseController: Application() {

    private val root = Pane().also {
        it.prefWidth = WIDTH
        it.prefHeight = HEIGHT

        it.stylesheets.add(Desktop::class.java.getResource("style.css")!!.toString())
    }

    private var offsetX: Double = 0.0
    private var offsetY: Double = 0.0

    override fun start(primaryStage: Stage?) {

        primaryStage?.let {
            it.scene = Scene(root, WIDTH, HEIGHT, Color.TRANSPARENT)
            it.scene.setOnMousePressed { event ->
                offsetX = primaryStage.x - event.screenX
                offsetY = primaryStage.y - event.screenY
            }
            it.scene.setOnMouseDragged { event ->
                it.scene.cursor = Cursor.MOVE
                primaryStage.x = event.screenX + offsetX
                primaryStage.y = event.screenY + offsetY
            }
            it.scene.setOnMouseReleased { _ -> it.scene.cursor = Cursor.DEFAULT }

            it.initStyle(StageStyle.TRANSPARENT)
            it.show()
        }

    }

}