package com.project.app.ui.component

import com.project.app.property.ProfileProperty
import com.project.app.repository.ProfileRepository
import com.project.app.ui.controller.WIDTH
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import kotlin.math.max

private const val DEFAULT_HEIGHT = 536.0

class AccountComponent {

    private val content = AnchorPane().also {
        it.prefWidth = 1183.0
        it.prefHeight = DEFAULT_HEIGHT
    }

    private val scroll = ScrollPane().also {
        it.layoutY = 140.0
        it.prefWidth = WIDTH
        it.prefHeight = 539.0

        it.content = content
    }

    fun init(root: Pane) {

        root.children.add(scroll)

        val profileRepository = ProfileRepository()
        val profiles = profileRepository.findAll()

        if (profiles.isNotEmpty()) {
            drawingProfiles(profiles)
        } else drawingNotFound()
    }

    fun clear(root: Pane) {
        content.children.removeAll()
        root.children.remove(scroll)
    }

    private fun drawingNotFound() {

        val image = ImageView().also {
            it.id = "404"
            it.layoutX = 464.0
            it.layoutY = 74.0
            it.fitWidth = 256.0
            it.fitHeight = 256.0
        }

        val title = Label("Тут пока что пусто..").also {
            it.id = "404-title"
            it.layoutX = 502.0
            it.layoutY = 318.0
        }

        val description = Label("Добавьте аккаунты для отслеживания предметов, после они появятся в этом поле").also {
            it.id = "404-description"
            it.layoutX = 285.0
            it.layoutY = 342.0
        }

        content.children.addAll(image, title, description)
        content.prefHeight = DEFAULT_HEIGHT
    }

    private fun drawingProfiles(profiles: List<ProfileProperty>) {

        var counterVertical = 0
        var counterHorizontal = 0

        profiles.forEach {
            val pane = getProfilePane(it).also { p ->
                p.layoutX = 26.0 + (286.0 * counterVertical++)
                p.layoutY = 14.0 + (126.0 * counterHorizontal)
                enterProfilePane(p, it)
            }

            if (counterVertical >= 4) {
                counterVertical = 0
                counterHorizontal++
            }
            content.children.add(pane)
        }

        content.prefHeight = max(DEFAULT_HEIGHT, 134.0 * counterVertical)
    }

    private fun getProfilePane(profileProperty: ProfileProperty): Pane {

        val pane = Pane().also {
            it.id = "account"
        }

        if (profileProperty.frame != null) {

            val frame = ImageView(profileProperty.frame).also {
                it.layoutX = 20.0
                it.layoutY = 14.0
                it.fitWidth = 90.0
                it.fitHeight = 90.0
            }
            pane.children.add(frame)

        }

        val avatar = ImageView(profileProperty.avatar).also {
            it.layoutX = 30.0
            it.layoutY = 24.0
            it.fitWidth = 70.0
            it.fitHeight = 70.0
        }

        val username = Label(profileProperty.steam?.accountName).also {
            it.id = "account-first"
            it.layoutX = 124.0
            it.layoutY = 20.0
        }

        val hintUsername = Label("Логин аккаунта").also {
            it.id = "account-second"
            it.layoutX = 140.0
            it.layoutY = 37.0
        }

        val cost = Label(profileProperty.inventory.summa.toString()).also {
            it.id = "account-first"
            it.layoutX = 124.0
            it.layoutY = 65.0
        }

        val costHint = Label("Стоимость инвентаря").also {
            it.id = "account-second"
            it.layoutX = 125.0
            it.layoutY = 85.0
        }

        pane.children.addAll(avatar, username, hintUsername, cost, costHint)
        return pane
    }

    private fun enterProfilePane(profilePane: Pane, profileProperty: ProfileProperty) {

        val pane = Pane().also {
            it.id = "mouse-entered-account"
        }

        val text = Label("Намжите, чтобы посмотреть информацию о профиле").also {
            it.id = "mouse-entered-text"
            it.layoutX = 45.0
            it.layoutY = 42.0
        }

        val remove = Pane().also {
            it.layoutX = 242.0
            it.layoutY = 14.0
            it.prefWidth = 24.0
            it.prefHeight = 24.0
            it.cursor = Cursor.HAND
            it.opacity = 0.6

            it.setOnMouseEntered { _ -> it.opacity = 1.0 }
            it.setOnMouseExited { _ -> it.opacity = 0.6 }

            ImageView().also { img ->
                img.id = "close"
                img.fitWidth = 24.0
                img.fitHeight = 24.0

                it.children.add(img)
            }
        }

        pane.children.addAll(text, remove)

        profilePane.setOnMouseEntered { profilePane.children.add(pane) }
        profilePane.setOnMouseExited { profilePane.children.removeIf { it.id == "mouse-entered-account" } }
    }

}