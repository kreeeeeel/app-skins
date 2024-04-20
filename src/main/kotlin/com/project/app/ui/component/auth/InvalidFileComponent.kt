package com.project.app.ui.component.auth

import com.project.app.ui.component.BaseComponent
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import java.awt.Desktop
import java.io.File
import java.util.concurrent.CompletableFuture
import kotlin.math.max

class InvalidFileComponent(
    private val files: List<File>
): BaseComponent() {

    private val block = Pane().also {
        it.id = "invalidFiles"
        it.layoutX = 450.0
        it.layoutY = 150.0

        val hint = Label("Данные файлы не прошли проверку, чтобы открыть эти файлы, выберите один и нажмите на него.").also { l ->
            l.id = "invalidFilesHint"
            l.layoutX = 25.0
            l.layoutY = 340.0
        }

        it.children.add(hint)
        pane.children.add(it)
    }

    private val field = Pane().also {
        it.id = "invalidFilesField"
        it.layoutX = 25.0
        it.layoutY = 14.0

        block.children.addAll(it)
    }

    private val scroll = ScrollPane().also {
        it.prefWidth = 250.0
        it.prefHeight = 310.0
        it.content = AnchorPane().also { ap ->
            ap.prefWidth = 235.0
            ap.prefHeight = 308.0
        }

        field.children.add(it)
    }

    override fun init() {
        CompletableFuture.supplyAsync { initScroll() }
        super.init()
    }

    private fun initScroll() {
        val content = scroll.content as AnchorPane
        var count = 0

        try {
            Platform.runLater {
                files.forEach {
                    val pane = getPane(it).also { p -> p.layoutY = 14.0 + (50 * count++) }
                    content.prefHeight = max(content.prefHeight, 14.0 + (50*count))
                    content.children.add(pane)
                }
            }
        } catch (ignored: Exception) {}
    }

    private fun getPane(file: File) = Pane().also {
        it.id = "invalidFileData"
        it.layoutX = 14.0

        val icon = ImageView().also { img ->
            img.id = "file"
            img.layoutX = 8.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
            img.layoutY = 8.0
        }

        val text = Label(file.name).also { l ->
            l.id = "invalidFileDataText"
            l.layoutX = 40.0
            l.layoutY = 10.0
        }

        it.setOnMouseClicked { Desktop.getDesktop().open(file) }
        it.children.addAll(icon, text)
    }

}