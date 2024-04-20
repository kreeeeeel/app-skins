package com.project.app.ui.component.auth

import com.project.app.data.MaFileData
import com.project.app.service.mafile.ImportFile
import com.project.app.service.mafile.impl.DefaultImportFile
import com.project.app.ui.component.BaseComponent
import com.project.app.ui.component.message.LoadingComponent
import com.project.app.ui.component.message.MessageComponent
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File
import java.util.concurrent.CompletableFuture

@Suppress("unused")
class PasswordComponent(
    private val maFileData: MaFileData
): BaseComponent() {

    private val block = Pane().also {
        it.id = "import"
        it.layoutX = 450.0
        it.layoutY = 172.0

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

        val description = Label("Метод ввода пароля").also { l ->
            l.id = "description-ma-file"
            l.layoutX = 80.0
            l.layoutY = 34.0
        }

        val hint = Label("Для ввода с клавиатуры, нажмите кнопку 'Ручной ввод'").also { l ->
            l.id = "import-keyboard-hint"
            l.layoutX = 25.0
            l.layoutY = 321.0
        }

        it.children.addAll(logo, title, description, hint)
        pane.children.addAll(it)
    }

    private val field = Pane().also {
        it.id = "importFile"
        it.layoutX = 25.0
        it.layoutY = 77.0

        val logo = ImageView().also { img ->
            img.id = "drag"
            img.layoutX = 77.0
            img.layoutY = 25.0
            img.fitWidth = 96.0
            img.fitHeight = 96.0
        }

        val hint = Label("Перетащите файл с паролями, запись должна быть в формате username:password").also{ l ->
            l.id = "import-file-hint"
            l.layoutX = 15.0
            l.layoutY = 117.0
        }

        it.setOnDragOver { event ->
            if (event.dragboard.hasFiles()) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY)
            }
            event.consume()
        }

        it.setOnDragDropped { event ->
            if (event.dragboard.hasFiles()) {
                event.isDropCompleted = true

                val file = event.dragboard.files[0]
                if (file.name.endsWith(".txt")) {
                    passwordFile(file)
                }
            }
            event.consume()
        }

        it.setOnDragEntered { event ->
            if (event.dragboard.hasFiles()) {
                it.id = "importFileDrag"
                hint.text = "Я очень сильно надеюсь, что это файл .txt и в нем действительно пароли"
            }
        }

        it.setOnDragExited { _ ->
            it.id = "importFile"
            hint.text = "Перетащите файл с паролями, запись должна быть в формате username:password"
        }

        it.children.addAll(logo, hint)
        block.children.add(it)
    }

    private val choose = Button("Выбрать файл").also {
        it.layoutX = 15.0
        it.layoutY = 186.0

        it.setOnMouseClicked { showOpenDialog() }

        field.children.add(it)
    }

    private val keyboard = Label("Нажмите для ввода в ручную").also {
        it.id = "import-keyboard"
        it.layoutX = 52.0
        it.layoutY = 324.0

        it.setOnMouseClicked { keyboardPassword() }
        block.children.add(it)
    }

    private val invalid = Pane().also {
        it.id = "invalid-data"
        it.layoutX = 252.0
        it.layoutY = 593.0
        it.isVisible = maFileData.invalid.isNotEmpty()

        val icon = ImageView().also { img ->
            img.id = "error"
            img.fitWidth = 48.0
            img.fitHeight = 48.0
            img.layoutX = 14.0
            img.layoutY = 8.0
        }

        val text = Label("${maFileData.invalid.size} файл(-ов) из ${maFileData.size} оказались невалидными!").also { l ->
            l.id = "invalid-data-text"
            l.layoutX = 73.0
            l.layoutY = 21.0
        }

        val button = Button("Посмотреть").also { b ->
            b.id = "look"
            b.layoutX = 536.0
            b.layoutY = 14.0

            b.setOnMouseClicked { InvalidFileComponent(maFileData.invalid).init() }
        }

        it.children.addAll(icon, text, button)
        pane.children.add(it)
    }

    private fun showOpenDialog() {
        val fileChooser = FileChooser().also {
            it.title = "Выберите файл"
            it.extensionFilters.add(FileChooser.ExtensionFilter("Txt File", "*.txt"))
        }

        val stage = choose.scene.window as Stage
        val selectedFile = fileChooser.showOpenDialog(stage)
        if (selectedFile != null){
            passwordFile(selectedFile)
        }
    }

    private fun passwordFile(file: File) {
        val root = pane.parent as Pane

        val loading = LoadingComponent()
        loading.initialize()

        CompletableFuture.supplyAsync {
            val importFile: ImportFile = DefaultImportFile()
            val result = importFile.getPassword(file, maFileData)

            Platform.runLater {
                if (result.users.isEmpty()) {
                    val message = MessageComponent()
                    message.drawErrorMessage("В этом файле небыло найдено паролей..")

                    loading.clear()

                } else if (result.invalid.isNotEmpty()) {

                    root.children.remove(pane)
                    loading.clear()

                    val component = InvalidPasswordComponent(result)
                    component.init()

                } else SteamComponent(result.users.values.toList()).start()
            }
        }

    }

    private fun keyboardPassword() {

        val component = KeyboardPasswordComponent(maFileData.data)
        component.init()

    }

}