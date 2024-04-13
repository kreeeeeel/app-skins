package com.project.app.ui.component.auth

import com.project.app.ui.component.BaseComponent
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import kotlin.math.max

class InvalidAuthComponent(
    private val size: Int,
    private val validCount: Int,
    private val invalid: List<String>
): BaseComponent() {

    private val block = Pane().also {
        it.id = "invalidAuth"
        it.layoutX = 450.0
        it.layoutY = 150.0

        val icon = ImageView().also { img ->
            img.id = "steam"
            img.layoutX = 25.0
            img.layoutY = 14.0
            img.fitWidth = 48.0
            img.fitHeight = 48.0
        }

        val title = Label("Итог: $validCount из $size").also { l ->
            l.id = "invalidAuthTitle"
            l.layoutX = 81.0
            l.layoutY = 22.0
        }

        val desc = Label("${invalid.size} аккаунт(-ов) не валидны").also { l ->
            l.id = "invalidAuthDesc"
            l.layoutX = 81.0
            l.layoutY = 40.0
        }

        val hint = Label("Проверьте валидность этих аккаунтов, возможно Steam отказал в авторизации из-за частых запросов, попробуйте позже.").also { l ->
            l.id = "invalidAuthHint"
            l.layoutX = 25.0
            l.layoutY = 323.0
        }

        it.children.addAll(icon, title, desc, hint)
        pane.children.add(it)
    }

    private val field = Pane().also {
        it.id = "invalidAuthField"
        it.layoutX = 25.0
        it.layoutY = 77.0

        block.children.add(it)
    }

    private val scroll = ScrollPane().also {
        it.prefWidth = 250.0
        it.prefHeight = 235.0
        it.content = AnchorPane().also { ap ->
            ap.prefWidth = 235.0
            ap.prefHeight = 233.0
        }

        field.children.add(it)
    }

    override fun init(root: Pane) {
        initScroll()
        super.init(root)
    }

    private fun initScroll() {
        val content = scroll.content as AnchorPane
        var count = 0

        invalid.forEach {
            val pane = getInvalid(it).also { p -> p.layoutY = 14.0 + (50*count++) }
            content.children.add(pane)
        }
        content.prefHeight = max(content.prefHeight, 14.0 + (50*count))
    }

    private fun getInvalid(username: String) = Pane().also {
        it.id = "dataInvalidPass"
        it.layoutX = 14.0

        val icon = ImageView().also { img ->
            img.id = "off"
            img.layoutX = 8.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
            img.layoutY = 8.0
        }

        val text = Label(username).also { l ->
            l.id = "invalidAuthTitle"
            l.layoutX = 40.0
            l.layoutY = 10.0
        }

        it.children.addAll(icon, text)
    }

}