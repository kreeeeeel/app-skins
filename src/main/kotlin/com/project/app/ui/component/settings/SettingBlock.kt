package com.project.app.ui.component.settings

import com.project.app.models.ConfigModel
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

abstract class SettingBlock(
    private val name: String,
    private val id: String,
    val config: ConfigModel
) {

    val block = Pane().also {
        it.id = "settingsBlock"
        it.layoutX = 17.0

        val icon = ImageView().also{ img ->
            img.id = id
            img.layoutX = 10.0
            img.layoutY = 10.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        val text = Label(name).also{ l ->
            l.id = "settingsBlockText"
            l.layoutX = 44.0
            l.layoutY = 13.0
        }

        it.children.addAll(icon, text)
    }

    abstract fun init(): Double

}