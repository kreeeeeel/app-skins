package com.project.app.ui.component.account

import com.project.app.models.ProfileModel
import com.project.app.models.SteamModel
import com.project.app.repository.MaFileRepository
import com.project.app.repository.ProfileRepository
import com.project.app.service.driver.impl.DefaultDriver
import com.project.app.ui.component.message.CenterComponent
import com.project.app.ui.component.message.LoadingComponent
import com.project.app.ui.component.notify.NotifyComponent
import com.project.app.ui.controller.BaseController.Companion.root
import com.project.app.ui.controller.WIDTH
import javafx.application.Platform
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.concurrent.CompletableFuture
import kotlin.math.max

private const val DEFAULT_HEIGHT = 536.0

class AccountComponent {

    init {

        val search = Pane().also {
            it.id = "searchPane"
            it.layoutX = 27.0
            it.layoutY = 86.0

            val icon = ImageView().also { img ->
                img.id = "search"
                img.layoutX = 14.0
                img.layoutY = 6.0
                img.fitWidth = 24.0
                img.fitHeight = 24.0
            }

            val textField = TextField().also { tf ->
                tf.id = "searchTextField"
                tf.layoutX = 38.0
                tf.layoutY = 2.0
                tf.promptText = "Поиск аккаунта по логину"

                tf.textProperty().addListener { _, _, newValue -> search(newValue.lowercase())}
                tf.focusedProperty().addListener { _, _, newValue -> searchResult.isVisible = newValue }
            }

            it.children.addAll(icon, textField)
        }

        root.children.add(search)

    }

    private var scroll: ScrollPane? = null

    private val searchResult = Label().also {
        it.id = "searchResult"
        it.layoutX = 33.0
        it.layoutY = 127.0
        it.isVisible = false

        root.children.add(it)
    }

    companion object { var havingAccounts: List<Pane> = emptyList() }

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

        havingAccounts = panes
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

            val load = LoadingComponent()
            load.initialize()

            CompletableFuture.supplyAsync {

                val maFileRepository = MaFileRepository()
                val property = maFileRepository.find(profileModel.username)
                val notifyComponent = NotifyComponent()

                if (property == null) {
                    Platform.runLater {
                        notifyComponent.failure("Не удалось авторизоваться в аккаунт Steam")
                        load.clear()
                    }
                    return@supplyAsync
                }

                val steamModel = SteamModel(profileModel.username, profileModel.password, property.sharedSecret)
                if (!steamModel.loggedIn()) {
                    Platform.runLater {
                        notifyComponent.failure("Произошла ошибка при авторизации в аккаунт Steam")
                        load.clear()
                    }
                } else {

                    val split = steamModel.steamCookie!!.split("=")
                    val name = split[0]
                    val value = split[1]

                    val center = CenterComponent("Браузер открывается!")
                    Platform.runLater {
                        center.show()
                        load.clear()
                    }

                    DefaultDriver().openBrowseProfile(name, value)
                }
            }
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

    private fun search(login: String) {

        Platform.runLater {

            scroll = root.children.firstOrNull{ it.id == "scroll-accounts" } as? ScrollPane
                ?: getScrollPane().also { root.children.add(it) }

            val content = scroll?.content as AnchorPane
            content.children.clear()

            var counterVertical = 0
            var counterHorizontal = 0

            val find = havingAccounts.filter {
                val pane = it
                val label = pane.children.first { l -> l.id == "account-first" } as Label

                label.text.startsWith(login)
            }.map {
                it.layoutX = 26.0 + (286.0 * counterVertical++)
                it.layoutY = 14.0 + (126.0 * counterHorizontal)

                if (counterVertical >= 4) {
                    counterVertical = 0
                    counterHorizontal++
                }

                return@map it
            }

            searchResult.text = "Найдено результатов: ${find.size}"
            if (find.isEmpty()) {

                val image = ImageView().also {
                    it.id = "badsearch"
                    it.layoutX = 550.0
                    it.layoutY = 150.0
                    it.fitWidth = 100.0
                    it.fitHeight = 100.0
                }

                val title = Label("Поиск не показал результатов..").also {
                    it.id = "404-title"
                    it.layoutX = 460.0
                    it.layoutY = 275.0
                }
                content.children.addAll(image, title)
                //searchResult.isVisible = find.size != havingAccounts.size

            } else content.children.addAll(find)

            content.prefHeight = max(DEFAULT_HEIGHT, 134.0 * counterVertical)
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