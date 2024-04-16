package com.project.app.ui.component.account

import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

class FilterComponent {

    private val filter = Pane().also{
        it.id = "filterPane"
        it.layoutX = 27.0
        it.layoutY = 86.0

        val icon = ImageView().also { img ->
            img.id = "filter"
            img.layoutX = 14.0
            img.layoutY = 10.0
            img.fitWidth = 16.0
            img.fitHeight = 16.0
        }

        val text = Label("Фильтр").also { l ->
            l.id = "filterText"
            l.layoutX = 63.0
            l.layoutY = 9.0
        }

        it.setOnMouseEntered { filterMenu.isVisible = true }
        it.children.addAll(icon, text)
    }

    private val filterMenu = Pane().also {
        it.id = "filterMenu"
        it.isVisible = false
        it.layoutX = 27.0
        it.layoutY = 86.0

        val date = getPointFilter("date", "Дата добавления")
        val login = getPointFilter("login", "Дата посл. входа").also { p ->
            p.layoutY = 36.0
        }
        val calculate = getPointFilter("calculate", "Стоимость").also { p ->
            p.layoutY = 72.0
        }

        it.setOnMouseExited { _ -> it.isVisible = false }

        it.focusedProperty().addListener { _, _, isFocused ->
            if (!isFocused && !it.children.any { node -> node.isFocused }) {
                it.isVisible = false
            }
        }
        it.children.addAll(date, login, calculate)
    }

    fun initialize(root: Pane) {
        root.children.addAll(filter, filterMenu)
    }

    private fun getPointFilter(id: String, value: String): Pane = Pane().also{
        it.id = "filterPointPane"

        val icon = ImageView().also { img ->
            img.id = id
            img.layoutX = if (id == "login") 12.0 else 14.0
            img.layoutY = 10.0
            img.fitWidth = 16.0
            img.fitHeight = 16.0
        }

        val text = Label(value).also { l ->
            l.id = "filterText"
            l.layoutX = if (id == "calculate") 55.0 else 43.0
            l.layoutY = 9.0
        }

        it.children.addAll(icon, text)
    }

}