package com.project.app.ui.component.account

import com.project.app.models.ProfileModel
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

object Account {

    fun getAccountPane(profileModel: ProfileModel): Pane {

        val pane = Pane().also {
            it.id = "account"
        }

        val avatar = ImageView(profileModel.photo).also {
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
            it.layoutX = 140.0
            it.layoutY = 37.0
        }

        val cost = Label(( profileModel.inventory ).toString()).also {
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