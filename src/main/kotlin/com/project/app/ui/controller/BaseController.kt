package com.project.app.ui.controller

import com.project.app.DESCRIPTION
import com.project.app.Desktop
import com.project.app.TITLE
import com.project.app.ui.component.tray.TrayComponent
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.event.EventHandler


const val WIDTH = 1200.0
const val HEIGHT = 700.0

const val ICON_TRAY = "files/logo.png"

private val HINT_TASK = """
    Данный блок показывает, что сейчас приложение выполняет. 
    Приложение в фоном режиме смотрит, на каких аккаунтах можно начать продавать инвентарь. 
    Если какие-то предмете подлежат выгодной продаже, вам придет уведомление.
""".trimIndent()

open class BaseController: Application() {

    val root = Pane().also {
        it.prefWidth = WIDTH
        it.prefHeight = HEIGHT

        val logo = ImageView().also { img ->
            img.id = "logo"
            img.layoutX = 28.0
            img.layoutY = 21.0
            img.fitWidth = 48.0
            img.fitHeight = 48.0
        }

        val title = Label(TITLE).also { l ->
            l.id = "title"
            l.layoutX = 82.0
            l.layoutY = 30.0
        }

        val description = Label(DESCRIPTION).also { l ->
            l.id = "description"
            l.layoutX = 82.0
            l.layoutY = 47.0
        }

        it.children.addAll(logo, title, description)
        it.stylesheets.add(Desktop::class.java.getResource("style.css")!!.toString())
    }

    private val task = Pane().also {
        it.id = "task"
        it.layoutX = 400.0
        it.layoutY = 33.0

        showTaskHint(it)
    }

    private val taskText = Label("Ожидание запуска задачи..").also {
        it.id = "task-text"
        it.layoutX = 29.0
        it.layoutY = 7.0

        task.children.add(it)
    }

    private val taskIcon = ImageView().also {
        it.id = "orange"
        it.fitWidth = 12.0
        it.fitHeight = 12.0
        it.layoutX = 10.0
        it.layoutY = 9.0

        task.children.add(it)
    }

    private val collapse = getFooterRight(false)
    private val close = getFooterRight(true)

    private var offsetX: Double = 0.0
    private var offsetY: Double = 0.0

    private var stage: Stage? = null

    override fun start(primaryStage: Stage?) {

        close.setOnMouseClicked { TrayComponent().init(root) }
        collapse.setOnMouseClicked { primaryStage?.isIconified = true }

        root.children.addAll(task, close, collapse)

        primaryStage?.let {
            it.scene = Scene(root, WIDTH, HEIGHT, Color.TRANSPARENT)
            /* Event на передвежение сцены с зажатием мышки */
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

            /* Event который используется при закрытии приложения из панели задач */
            it.onCloseRequest = EventHandler { event ->
                event.consume()

                // Вызываем трей компонент
                val trayComponent = TrayComponent()
                trayComponent.init(root)

                // Передвигаем сцены на передний экран
                it.toFront()
            }

            stage = it

            // Через try инициализируем стиль и добавление иконки приложению
            try {
                it.title = TITLE
                it.initStyle(StageStyle.TRANSPARENT)
                it.icons.add(Image(Desktop::class.java.getResourceAsStream(ICON_TRAY)))
            } finally {
                it.show()
                it.toFront()
            }
        }
    }


    /**
     * Отображает подсказку для задачи при наведении курсора мыши на указанную панель.
     *
     * @param taskPane Панель, на которой отображается подсказка для задачи.
     */
    private fun showTaskHint(taskPane: Pane) = Platform.runLater {
        val pane = Pane().also {
            it.id = "task-hint"
            it.layoutX = 290.0
            it.layoutY = 70.0

            val text = Label(HINT_TASK).also { l ->
                l.id = "task-hint-text"
                l.layoutX = 0.0
                l.layoutY = 10.0
            }

            it.children.add(text)
        }

        taskPane.setOnMouseEntered { root.children.add(pane) }
        taskPane.setOnMouseExited { root.children.removeIf { it.id == "task-hint" } }
    }

    companion object {

        fun getFooterRight(isClose: Boolean): Pane = Pane().also {
            it.layoutX = if (isClose) 1136.0 else 1090.0
            it.layoutY = 22.0
            it.prefWidth = 34.0
            it.prefHeight = 34.0
            it.cursor = Cursor.HAND
            it.opacity = 0.6

            it.setOnMouseEntered { _ -> it.opacity = 1.0 }
            it.setOnMouseExited { _ -> it.opacity = 0.6 }

            ImageView().also { img ->
                img.id = if (isClose) "close" else "collapse"
                img.fitWidth = 34.0
                img.fitHeight = 34.0

                it.children.add(img)
            }
        }

    }

}