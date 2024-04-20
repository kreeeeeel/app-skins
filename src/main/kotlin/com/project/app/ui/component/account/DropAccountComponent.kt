package com.project.app.ui.component.account

import com.project.app.models.ProfileModel
import com.project.app.repository.ProfileRepository
import com.project.app.ui.component.BaseComponent
import com.project.app.ui.component.message.MessageComponent
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import com.project.app.ui.controller.BaseController.Companion.root

class DropAccountComponent: BaseComponent() {

    private val block = Pane().also {
        it.id = "block-drop-account"
        it.layoutX = 400.0
        it.layoutY = 250.0

        val text = Label("Вы действительно хотите удалить этот аккаунт?").also { l ->
            l.id = "block-drop-account-text"
            l.layoutX = 26.0
            l.layoutY = 90.0
        }

        it.children.add(text)
    }

    private val drop = Button("Удалить").also {
        it.id = "drop-account"
        it.layoutX = 50.0
        it.layoutY = 126.0
    }

    private val cancel = Button("Отмена").also {
        it.id = "cancel-drop-account"
        it.layoutX = 210.0
        it.layoutY = 126.0

    }

    override fun init() = Platform.runLater {
        cancel.setOnMouseClicked { root.children.remove(pane) }
        block.children.addAll(drop, cancel)
        pane.children.addAll(block)
        super.init()
    }

    fun setProfile(profileModel: ProfileModel) = Platform.runLater{
        val pane = Account.getAccountPane(profileModel).also {
            it.layoutX = 60.0
            it.layoutY = -40.0
        }

        drop.setOnMouseClicked { dropAccount(profileModel) }
        block.children.add(pane)
    }

    private fun dropAccount(profileModel: ProfileModel) {
        val profileRepository = ProfileRepository()
        val messageComponent = MessageComponent()

        if (!profileRepository.remove(profileModel)) {
            messageComponent.drawErrorMessage("Не удалось удалить аккаунт..")

        } else {

            root.children.remove(pane)
            val accountComponent = AccountComponent()

            messageComponent.drawSuccessMessage("Аккаунт был успешно удален!")
            accountComponent.initializeOrUpdate()
        }
    }

}