package com.project.app.ui.component.account

import com.project.app.Desktop
import com.project.app.models.ProfileModel
import com.project.app.repository.AvatarRepository
import com.project.app.repository.MaFileRepository
import com.project.app.service.steam.SteamGuard
import com.project.app.service.steam.impl.DefaultSteamGuard
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

object Account {

    private val avatarRepository = AvatarRepository()
    private val maFileRepository = MaFileRepository()
    private val steamGuard: SteamGuard = DefaultSteamGuard()

    fun getAccountPane(profileModel: ProfileModel): Pane {

        val pane = Pane().also {
            it.id = "account"
        }

        val property = maFileRepository.find(profileModel.username)

        val secret = Label(property?.sharedSecret ?: "").also { l ->
            l.id = "secret"
            l.isVisible = false
        }

        val photo = avatarRepository.getAvatar(profileModel.username)
            ?: Image(Desktop::class.java.getResourceAsStream("files/nophoto.jpg"))

        val avatar = ImageView(photo).also {
            it.layoutX = 30.0
            it.layoutY = 24.0
            it.fitWidth = 70.0
            it.fitHeight = 70.0
        }

        val username = Label(profileModel.username).also {
            it.id = "account-first"
            it.layoutX = 124.0
            it.layoutY = 20.0
        }

        val hintUsername = Label("Логин аккаунта").also {
            it.id = "account-second"
            it.layoutX = 145.0
            it.layoutY = 37.0
        }

        val guard = if (property == null) "Нет данных" else steamGuard.getCode(secret.text)
        val cost = Label(guard).also {
            it.id = "account-first"
            it.layoutX = 124.0
            it.layoutY = 65.0
        }

        val costHint = Label("Текущий код Guard").also {
            it.id = "account-second"
            it.layoutX = 130.0
            it.layoutY = 85.0
        }

        pane.children.addAll(secret, avatar, username, hintUsername, cost, costHint)
        return pane
    }


}