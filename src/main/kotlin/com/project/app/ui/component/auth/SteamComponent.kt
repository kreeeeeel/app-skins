package com.project.app.ui.component.auth

import com.project.app.models.SteamModel
import com.project.app.data.ValidData
import com.project.app.models.ProfileModel
import com.project.app.repository.ConfigRepository
import com.project.app.repository.ProfileRepository
import com.project.app.service.steam.SteamProfile
import com.project.app.service.steam.impl.DefaultSteamProfile
import com.project.app.ui.component.AccountComponent
import com.project.app.ui.component.MessageComponent
import com.project.app.ui.controller.HEIGHT
import com.project.app.ui.controller.WIDTH
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import java.util.*
import java.util.concurrent.CompletableFuture

private const val WAIT = "Пожалуйста, подождите, пока приложение авторизуется"
private const val ATTEMPT = "Попытка входа %d из %d"

class SteamComponent(
    private val validData: List<ValidData>
) {

    private var currentAttempt: Int = 1
    private val config = ConfigRepository().find()

    private val pane = Pane().also{
        it.id = "background"
        it.prefWidth = WIDTH
        it.prefHeight = HEIGHT
    }

    private val block = Pane().also {
        it.id = "authSteam"
        it.layoutX = 410.0
        it.layoutY = 320.0

        val icon = ImageView().also { img ->
            img.id = "steam"
            img.fitHeight = 48.0
            img.fitWidth = 48.0
            img.layoutX = 8.0
            img.layoutY = 6.0
        }

        val title = Label("Вход в аккаунт Steam").also { l ->
            l.id = "titleAuthSteam"
            l.layoutX = 65.0
            l.layoutY = 10.0
        }

        it.children.addAll(icon, title)
        pane.children.add(it)
    }

    private val username = Label(validData[0].username).also {
        it.id = "accountAuthSteam"
        it.layoutX = 65.0
        it.layoutY = 30.0

        block.children.add(it)
    }

    private val attempt = Label().also {
        it.id = "attemptAuthSteam"
        it.layoutX = 511.0
        it.layoutY = 231.0

        pane.children.add(it)
    }

    private val wait = Label(WAIT).also {
        it.id = "waitAuthSteam"
        it.layoutX = 349.0
        it.layoutY = 251.0

        pane.children.add(it)
    }

    private val steamProfile: SteamProfile = DefaultSteamProfile()
    private val profileRepository = ProfileRepository()

    private val timer = Timer()

    fun start(root: Pane) {

        root.children.let {
            it.removeIf { node -> node.id == "background" }
            it.add(pane)
        }
        timer.scheduleAtFixedRate(SteamTask(wait), 500, 500)

        val invalid = mutableListOf<String>()
        CompletableFuture.supplyAsync{
            for (data in validData) { if (!auth(data)) invalid.add(data.username) }
        }.thenRun { finalizeAuth(invalid) }
    }

    private fun auth(data: ValidData): Boolean {
        val steamModel = SteamModel(data.username, data.password!!, data.sharedSecret)
        do {
            Platform.runLater {
                attempt.text = String.format(ATTEMPT, currentAttempt, config.attemptSteam)
                username.text = data.username
            }
        } while (!steamModel.loggedIn() && currentAttempt++ < config.attemptSteam)

        currentAttempt = 1
        if (steamModel.isLoggedIn) {

            val response = steamProfile.getProfileData(steamModel.steamId!!) ?: return false

            val profile = ProfileModel(
                username = data.username,
                name = response.name,
                photo = response.avatar,
                password = data.password!!
            )
            return profileRepository.save(profile)
        }
        return false
    }

    private fun finalizeAuth(users: List<String>) {

        val root = pane.parent as Pane
        val component = MessageComponent(root)
        Platform.runLater {

            timer.cancel()
            root.children.remove(pane)

            if (users.isEmpty()) {
                component.drawSuccessMessage("Авторизация в аккаунты прошла успешно!")
            } else if (users.size == validData.size) {
                component.drawErrorMessage("Не удалось войти во все аккаунты!!")
            } else {
                val invalidAuthComponent = InvalidAuthComponent(validData.size, validData.size - users.size, users)
                invalidAuthComponent.init(root)
            }

            val accounts = AccountComponent(root)
            accounts.initializeOrUpdate()
        }

    }

    class SteamTask(
        private val wait: Label,
    ): TimerTask() {

        private var count = 0

        override fun run() {
            Platform.runLater {
                wait.text = WAIT + ".".repeat(count)
            }

            if (count++ >= 3) count = 0
        }
    }


}