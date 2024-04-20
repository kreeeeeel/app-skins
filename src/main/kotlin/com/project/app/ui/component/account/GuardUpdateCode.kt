package com.project.app.ui.component.account

import com.project.app.service.steam.SteamGuard
import com.project.app.service.steam.impl.DefaultSteamGuard
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import java.util.TimerTask
import com.project.app.ui.controller.BaseController.Companion.root

private const val TEXT = "Обновление Steam кодов через %02d сек."
private const val TIME_LIVE_GUARD = 30

class GuardUpdateCode: TimerTask() {

    private val pane = Pane().also {
        it.id = "guardComponent"
        it.layoutX = 27.0
        it.layoutY = 86.0

        val img = ImageView().also { img ->
            img.id = "guardImg"
            img.fitWidth = 16.0
            img.fitHeight = 16.0
            img.layoutX = 14.0
            img.layoutY = 10.0
        }

        it.children.add(img)
    }

    private val update = Label("Инициализация таймера обновления").also {
        it.id = "guardLabel"
        it.layoutX = 49.0
        it.layoutY = 9.0

        pane.children.add(it)
    }

    init {
        Platform.runLater { root.children.add(pane) }
    }

    private var seconds = TIME_LIVE_GUARD
    private val steamGuard: SteamGuard = DefaultSteamGuard()

    override fun run() {

        Platform.runLater { update.text = String.format(TEXT, seconds) }
        if (seconds-- <= 1) {

            Platform.runLater {
                val scroll = root.children.first { it.id == "scroll-accounts" } as ScrollPane
                val content = scroll.content as AnchorPane

                content.children
                    .filter { it.id == "account" }
                    .forEach { update(it as Pane) }

                val drop = root.children.firstOrNull { it.id == "background" }
                if (drop != null) {

                    val block = (drop as Pane).children.firstOrNull { it.id == "block-drop-account" }
                    if (block != null) {

                        val account = (block as Pane).children.firstOrNull { it.id == "account" }
                        if (account != null) {
                            update(account as Pane)
                        }

                    }

                }
            }

            seconds = TIME_LIVE_GUARD
        }

    }

    private fun update(pane: Pane) = Platform.runLater {
        val sharedSecret = pane.children.first { node -> node.id == "secret" } as Label
        if (sharedSecret.text != "") {
            val guard = pane.children.last { node -> node.id == "account-first" } as Label
            guard.text = steamGuard.getCode(sharedSecret.text)
        }
    }

}