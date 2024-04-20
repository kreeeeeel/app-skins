package com.project.app.ui.component.account

import com.project.app.models.ProfileModel
import com.project.app.repository.ProfileRepository
import com.project.app.service.driver.impl.DefaultDriver
import com.project.app.ui.component.message.CenterComponent
import com.project.app.ui.controller.BaseController.Companion.root
import com.project.app.ui.controller.WIDTH
import javafx.application.Platform
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.concurrent.CompletableFuture
import kotlin.math.max

private const val DEFAULT_HEIGHT = 536.0

class AccountComponent {
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

        val default = "Наведитесь, чтобы увидить действие"
        val pane = Pane().also {
            it.id = "mouse-entered-account"
        }

        val text = Label(default).also {
            it.id = "mouse-entered-text"
            it.layoutX = 24.0
            it.layoutY = 71.0
        }

        val browser = getEnterIcon("browser24x24").also { img -> img.layoutX = 62.0 }
        val copy = getEnterIcon("copy24x24").also { img -> img.layoutX = 104.0 }
        val guard = getEnterIcon("guard24x24").also { img -> img.layoutX = 148.0 }
        val bag = getEnterIcon("bag24x24").also { img -> img.layoutX = 189.0 }

        browser.setOnMouseEntered { _ -> text.text = "Нажмите, чтобы авторизоваться в браузере" }
        browser.setOnMouseExited { _ -> text.text = default }
        browser.setOnMouseClicked {

            val center = CenterComponent("Браузер открывается!")
            center.show()

            val name = profileModel.cookie.keys.elementAt(0)
            val value = profileModel.cookie.values.elementAt(0)

            CompletableFuture.supplyAsync { DefaultDriver().openBrowseProfile(name, value) }
        }

        copy.setOnMouseEntered { _ -> text.text = "Нажмите, чтобы скопировать логин:пароль" }
        copy.setOnMouseExited { _ -> text.text = default }
        copy.setOnMouseClicked { _ ->

            val center = CenterComponent("Логин и пароль скопированы!")
            center.show()

            val stringSelection = StringSelection(String.format("%s:%s", profileModel.username, profileModel.password))
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(stringSelection, null)
        }

        guard.setOnMouseEntered { _ -> text.text = "Нажмите, чтобы скопировать Steam Guard" }
        guard.setOnMouseExited { _ -> text.text = default }
        guard.setOnMouseClicked { _ ->

            val center = CenterComponent("Код Steam Guard скопирован!")
            center.show()

            val label = profilePane.children.last { it.id == "account-first" } as Label

            val stringSelection = StringSelection(label.text)
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(stringSelection, null)
        }

        bag.setOnMouseEntered { _ -> text.text = "Нажмите, чтобы удалить аккаунт" }
        bag.setOnMouseExited { _ -> text.text = default }
        bag.setOnMouseClicked {
            val dropAccountComponent = DropAccountComponent()
            dropAccountComponent.setProfile(profileModel)
            dropAccountComponent.init()
            dropAccountComponent.animate()
        }


        pane.children.addAll(text, browser, copy, guard, bag)

        profilePane.setOnMouseEntered { profilePane.children.add(pane) }
        profilePane.setOnMouseExited { profilePane.children.removeIf { it.id == "mouse-entered-account" } }
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

    private fun getEnterIcon(id: String) = Pane().also {
        it.layoutY = 32.0
        it.prefWidth = 24.0
        it.prefHeight = 24.0
        it.cursor = Cursor.HAND

        val img = ImageView().also { img ->
            img.id = id
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        it.children.add(img)
    }

}