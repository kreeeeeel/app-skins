package com.project.app.ui.component.settings.block

import com.project.app.models.ConfigModel
import com.project.app.models.Telegram
import com.project.app.models.TelegramBot
import com.project.app.service.telegram.BotInitialize
import com.project.app.ui.component.message.CenterComponent
import com.project.app.ui.component.message.LoadingComponent
import com.project.app.ui.component.message.MessageComponent
import com.project.app.ui.component.settings.SettingBlock
import com.project.app.ui.component.settings.SettingsComponent
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

class TelegramBlock(
    config: ConfigModel
): SettingBlock("Телеграм", "telegram", config) {

    /* Используются когда бот не привязан и не подтвержден */
    private val token: TextField = TextField().also {
        it.promptText = "Токен бота"
        it.layoutX = 19.0
        it.layoutY = 54.0

        it.textProperty().addListener { _, _, newValue -> tokenButton.isDisable = !isAlreadyToStart(newValue, botName.text)}
        it.setOnKeyReleased { event ->
            if (event.code == KeyCode.ENTER) {
                startBot(it.text, botName.text)
            }
        }
    }

    private val botName: TextField = TextField().also {
        it.promptText = "Имя бота"
        it.layoutX = 19.0
        it.layoutY = 103.0

        it.textProperty().addListener { _, _, newValue -> tokenButton.isDisable = !isAlreadyToStart(token.text, newValue)}
        it.setOnKeyReleased { event ->
            if (event.code == KeyCode.ENTER) {
                startBot(token.text, it.text)
            }
        }
    }

    private val tokenText = Label("У вас не привязан телеграм бот к приложению, чтобы привязать бота, укажите его токен и имя содержащае слово 'bot' и нажмите кнопку 'Начать' и следуйте дальнейшим инструкциям.").also {
        it.id = "telegramTokenText"
        it.layoutX = 15.0
        it.layoutY = 151.0
    }

    private val tokenButton = Button("Начать").also {
        it.isDisable = true
        it.layoutX = 30.0
        it.layoutY = 242.0

        it.setOnMouseClicked { startBot(token.text, botName.text) }
    }

    /* Используется для отображения подтверждения бота */
    private val confirmText = Label("Бот ожидает подтверждения, скопируйте код и отправьте его боту, или же нажмите 'Подтвердить'").also {
        it.id = "telegramConfirmText"
        it.layoutX = 19.0
        it.layoutY = 51.0
    }

    private val confirmIcon = ImageView().also {
        it.id = "telegramCopy"
        it.layoutX = 53.0
        it.layoutY = 126.0
        it.fitWidth = 34.0
        it.fitHeight = 34.0
    }

    private val confirmCode = Label(config.telegram?.bot?.code).also {
        it.id = "telegramConfirmCode"
        it.layoutX = 94.0
        it.layoutY = 124.0

        it.setOnMouseClicked { _ ->

            val center = CenterComponent("Код подтверждения скопирован!")
            center.show(root)

            val stringSelection = StringSelection(it.text)
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(stringSelection, null)
        }
    }

    private val confirmCodeText = Label("Нажмите, чтобы скопировать код").also {
        it.id = "telegramConfirmCodeText"
        it.layoutX = 37.0
        it.layoutY = 165.0
    }

    private val confirmButton = Button("Подтвердить").also {
        it.layoutX = 30.0
        it.layoutY = 191.0
    }


    override fun init(root: Pane): Double {

        this.root = root

        if (config.telegram == null || config.telegram?.bot == null){
            block.children.addAll(token, botName, tokenText, tokenButton)
            block.prefHeight = 290.0
        } else if (config.telegram?.isWaiting!!) {
            block.children.addAll(confirmText, confirmIcon, confirmCode, confirmCodeText, confirmButton)
            block.prefHeight = 250.0
        }

        return block.prefHeight
    }

    private fun isAlreadyToStart(token: String, botName: String): Boolean {

        val split = token.split(":")
        if (split.size != 2) {
            return false
        }

        if (!botName.lowercase().contains("bot")){
            return false
        }

        return true

    }

    private fun startBot(token: String, botName: String){

        if (!tokenButton.isDisable) {

            Platform.runLater {
                val loadingComponent = LoadingComponent(root)
                loadingComponent.initialize()

                val future = CompletableFuture.supplyAsync {
                    BotInitialize.init(token, botName)

                    if (BotInitialize.telegramBot == null || !BotInitialize.telegramBot?.isConnected!!) {
                        val component = MessageComponent(root)
                        component.drawErrorMessage("Не удалось запустить телеграм бота, проверьте валидность данных")
                    } else {

                        config.telegram = Telegram()
                        config.telegram?.bot = TelegramBot()
                        config.telegram?.bot?.name = botName
                        config.telegram?.bot?.token = token
                        config.telegram?.bot?.code = generateCode()
                        config.telegram?.isWaiting = true

                        config.save()
                        SettingsComponent().also {
                            it.root = root
                            it.refresh()
                        }

                    }
                }
                future.thenApply { Platform.runLater { loadingComponent.clear() } }
            }

        }

    }

    private fun generateCode(): String {
        val charPool : List<Char> = ('A'..'Z') + ('0'..'9')
        return (1..6)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}