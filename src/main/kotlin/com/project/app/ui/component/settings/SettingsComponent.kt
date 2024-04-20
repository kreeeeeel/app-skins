package com.project.app.ui.component.settings

import com.project.app.models.ConfigModel
import com.project.app.ui.component.BaseComponent
import com.project.app.ui.component.settings.block.TelegramBlock
import com.project.app.ui.controller.BaseController.Companion.root
import javafx.application.Platform
import javafx.scene.control.ScrollPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane

class SettingsComponent: BaseComponent() {

    private val config = ConfigModel().init()

    private val main = Pane().also{
        it.id = "settingsComponent"
        it.layoutX = 443.0
        it.layoutY = 50.0

        pane.children.add(it)
    }

    private val scroll = ScrollPane().also {
        it.id = "scroll"
        it.prefWidth = 315.0
        it.prefHeight = 600.0

        it.content = AnchorPane().also { ap ->
            ap.prefWidth = 300.0
            ap.prefHeight = 600.0
        }

        main.children.add(it)
    }

    private val blocks: List<SettingBlock> = listOf(
        TelegramBlock(config),
    )

    fun refresh() {
        val pane = root.children.firstOrNull { it.id == "background" } ?: return
        val settings = (pane as Pane).children.firstOrNull { it.id == main.id } ?: return
        val scroll = (settings as Pane).children.firstOrNull { it.id == "scroll" } ?: return

        val content = (scroll as ScrollPane).content as AnchorPane
        Platform.runLater {
            content.children.clear()
            draw(content)
        }
    }

    override fun init() {

        val content = scroll.content as AnchorPane

        draw(content)
        super.init()
    }

    private fun draw(content: AnchorPane) {
        var lastPosY = 14.0
        blocks.forEach {
            content.children.add(it.block)

            it.block.layoutY = lastPosY
            lastPosY += it.init()
        }

        content.prefHeight = lastPosY
    }

}