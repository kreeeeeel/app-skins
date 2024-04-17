package com.project.app.ui.controller

import com.project.app.ui.component.account.AccountComponent
import com.project.app.ui.component.account.GuardUpdateCode
import com.project.app.ui.component.auth.FileComponent
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.util.Timer

class AccountController: BaseController() {

    private val accountPlus = Pane().also {
        it.id = "add_account"
        it.layoutX = 1000.0
        it.layoutY = 85.0

        val icon = ImageView().also { img ->
            img.id = "plus"
            img.layoutX = 7.0
            img.layoutY = 6.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        val name = Label("Добавить аккаунт").also{ l ->
            l.id = "add_account_text"
            l.layoutX = 37.0
            l.layoutY = 9.0
        }

        it.children.addAll(icon, name)
    }

    override fun start(primaryStage: Stage?) {
        root.children.addAll(accountPlus)

        accountPlus.setOnMouseClicked {
            val fileComponent = FileComponent()
            fileComponent.init(root)
            fileComponent.animate()
        }

        val accountComponent = AccountComponent(root)
        accountComponent.initializeOrUpdate()

        Timer().scheduleAtFixedRate(GuardUpdateCode(root), 1000, 1000)

        super.start(primaryStage)
    }

}