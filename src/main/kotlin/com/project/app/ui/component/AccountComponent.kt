package com.project.app.ui.component

import com.project.app.property.ProfileProperty
import com.project.app.repository.ProfileRepository
import com.project.app.ui.controller.WIDTH
import javafx.application.Platform
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import java.awt.Desktop
import java.net.URI
import java.net.URL
import kotlin.math.max

private const val DEFAULT_HEIGHT = 536.0

class AccountComponent(
    private val root: Pane
) {

    private var scroll: ScrollPane? = null

    fun initializeOrUpdate() {
        scroll = root.children.firstOrNull{ it.id == "scroll-accounts" } as? ScrollPane
            ?: getScrollPane().also { root.children.add(it) }

        val content = scroll?.content as AnchorPane
        content.children.clear()

        val profileRepository = ProfileRepository()
        profileRepository.findAll().let {
            if (it.isNotEmpty()) drawingProfiles(it) else drawingNotFound()
        }
    }

    private fun drawingNotFound() = Platform.runLater {

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

        val content = scroll?.content as AnchorPane
        content.children.addAll(image, title, description)
        content.prefHeight = DEFAULT_HEIGHT
    }

    private fun drawingProfiles(profiles: List<ProfileProperty>) = Platform.runLater {

        var counterVertical = 0
        var counterHorizontal = 0

        val content = scroll?.content as AnchorPane

        val panes = profiles.map {
            val pane = getProfilePane(it).also { p ->
                p.layoutX = 26.0 + (286.0 * counterVertical++)
                p.layoutY = 14.0 + (126.0 * counterHorizontal)
                enterProfilePane(p, it)
            }

            if (counterVertical >= 4) {
                counterVertical = 0
                counterHorizontal++
            }

            return@map pane
        }

        content.children.addAll(panes)
        content.prefHeight = max(DEFAULT_HEIGHT, 134.0 * counterVertical)
    }

    private fun enterProfilePane(profilePane: Pane, profileProperty: ProfileProperty) = Platform.runLater {

        val pane = Pane().also {
            it.id = "mouse-entered-account"
        }

        val text = Label("Намжите, чтобы открыть профиль в браузере").also {
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
        pane.setOnMouseClicked {
            Desktop.getDesktop().browse(
                URI.create("https://steamcommunity.com/profiles/${profileProperty.steam?.session?.steamID}")
            )
        }

        remove.setOnMouseClicked {
            val dropAccountComponent = DropAccountComponent()
            dropAccountComponent.setProfile(profileProperty)
            dropAccountComponent.init(root)
            dropAccountComponent.animate()
        }
    }

    private fun getScrollPane() = ScrollPane().also {
        it.id = "scroll-accounts"
        it.layoutY = 140.0
        it.prefWidth = WIDTH
        it.prefHeight = 539.0
        it.content = AnchorPane().also { ap ->
            ap.prefWidth = 1183.0
            ap.prefHeight = DEFAULT_HEIGHT
        }
    }

    companion object {
        fun getProfilePane(profileProperty: ProfileProperty): Pane {

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

            val cost = Label(( profileProperty.inventory?.summa ?: 0.0 ).toString()).also {
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
    }

}