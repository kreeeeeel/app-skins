package com.project.app.ui.component.account

import com.project.app.models.ProfileModel
import com.project.app.repository.ProfileRepository
import com.project.app.ui.controller.WIDTH
import javafx.application.Platform
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
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

    private fun drawingProfiles(profiles: List<ProfileModel>) = Platform.runLater {

        var counterVertical = 0
        var counterHorizontal = 0

        val content = scroll?.content as AnchorPane

        val panes = profiles.map {
            val pane = Account.getAccountPane(it).also { p ->
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

    private fun enterProfilePane(profilePane: Pane, profileModel: ProfileModel) = Platform.runLater {

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

        remove.setOnMouseClicked {
            val dropAccountComponent = DropAccountComponent()
            dropAccountComponent.setProfile(profileModel)
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

}