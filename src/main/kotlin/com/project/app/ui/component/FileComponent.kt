package com.project.app.ui.component

import com.project.app.handler.MaFileHandler
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File


private const val HINT_TEXT = "Загрузите файл с расширением .maFile. После загрузки, в новом окне, укажите пароль от аккаунта"

class FileComponent: BaseComponent() {

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
            l.layoutY = 318.0
        }

        it.children.addAll(logo, title, description, hint)
        pane.children.addAll(it)
    }

    private val field = Pane().also {
        it.id = "ma-file"
        it.layoutX = 25.0
        it.layoutY = 87.0

        val logo = ImageView().also { img ->
            img.id = "drag"
            img.layoutX = 77.0
            img.layoutY = 25.0
            img.fitWidth = 96.0
            img.fitHeight = 96.0
        }

        it.children.addAll(logo)
        block.children.add(it)
    }

    private val hint: Label = Label("Перетащите файл .maFile в поле").also{
        it.id = "ma-file-text"
        it.layoutX = 25.0
        it.layoutY = 121.0

        field.children.add(it)
    }

    private val button = Button("Выбрать файл").also {
        it.layoutX = 15.0
        it.layoutY = 157.0

        field.children.add(it)
    }

    override fun init(root: Pane) = Platform.runLater {

        field.setOnDragOver { event ->
            if (event.dragboard.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY)
            }
            event.consume()
        }

        field.setOnDragDropped { event ->
            if (event.dragboard.hasFiles()) {
                event.isDropCompleted = true

                val file = event.dragboard.files[0]
                setupPassword(file, root)
            }
            event.consume()
        }

        field.setOnDragEntered { event ->
            if (event.dragboard.hasFiles()) {
                field.id = "ma-file-drag"
                hint.text = "Ловлю! Отпускай, я поймаю..."
            }
        }
        field.setOnDragExited {
            field.id = "ma-file"
            hint.text = "Перетащите файл .maFile в поле"
        }

        button.setOnMouseClicked { showOpenDialog(root) }

        super.init(root)
    }

    private fun showOpenDialog(root: Pane) {
        val fileChooser = FileChooser().also {
            it.title = "Выберите .maFile"
            it.extensionFilters.add(FileChooser.ExtensionFilter("Ma File", "*.maFile"))
        }

        val stage = button.scene.window as Stage
        val selectedFile = fileChooser.showOpenDialog(stage)
        if (selectedFile != null){
            setupPassword(selectedFile, root)
        }
    }

    private fun setupPassword(file: File, root: Pane) = Platform.runLater {

        val maFileHandler = MaFileHandler()
        val steamProperty = maFileHandler.getSteamProperty(file)

        if (steamProperty != null) {
            PasswordComponent(steamProperty).init(root)
        }
    }

}