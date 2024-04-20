package com.project.app.ui.component.settings.block

import com.project.app.models.ConfigModel
import com.project.app.models.Telegram
import com.project.app.models.TelegramBot
import com.project.app.service.logger.Logger
import com.project.app.service.logger.impl.DefaultLogger
import com.project.app.service.telegram.BotInitialize
import com.project.app.ui.component.message.CenterComponent
import com.project.app.ui.component.message.LoadingComponent
import com.project.app.ui.component.notify.NotifyComponent
import com.project.app.ui.component.settings.SettingBlock
import com.project.app.ui.component.settings.SettingsComponent
import com.project.app.ui.controller.BaseController.Companion.getFooterRight
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

class TelegramBlock(
    config: ConfigModel
): SettingBlock("Телеграм", "telegram", config) {

    private val logger: Logger = DefaultLogger()

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
            center.show()

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

        it.setOnMouseClicked { _ -> Desktop.getDesktop().browse(
            URI.create("https://t.me/${config.telegram?.bot?.name}?start=${config.telegram?.bot?.code}"))
        }
    }

    /* Используется для отображения подключенного бота */
    private val photo = Circle().also {
        it.layoutX = 54.0
        it.layoutY = 81.0
        it.radius = 32.0

        config.telegram?.user?.photo?.let { c ->
            it.fill = ImagePattern(Image(c))
        }
    }

    private val name = Label().also {
        it.id = "telegramName"
        it.layoutX = 97.0
        it.layoutY = 60.0
        it.text = "Не определенно"

        config.telegram?.user?.name?.let { c ->
            it.text = c
        }
    }

    private val username = Label().also {
        it.id = "telegramUserName"
        it.layoutX = 97.0
        it.layoutY = 81.0
        it.text = "404"

        config.telegram?.user?.username?.let { c ->
            it.text = "@$c"
        }
    }

    private val notifyIcon = ImageView().also {
        it.id = "green"
        it.layoutX = 26.0
        it.layoutY = 125.0
        it.fitHeight = 16.0
        it.fitWidth = 16.0

        config.telegram?.isNotify?.let { enabled ->
            it.id = if (enabled) "green" else "red"
        }
    }

    private val notifyText = Label("Не определенно").also {
        it.id = "telegramNotification"
        it.layoutX = 54.0
        it.layoutY = 124.0

        config.telegram?.isNotify?.let { enabled ->
            it.text = if (enabled) "Уведомления включены" else "Уведомления отключены"
        }
    }

    private val notifyBtn = Pane().also {
        it.id = "telegramNotifyOff"
        it.layoutX = 18.0
        it.layoutY = 150.0

        val icon = ImageView().also { img ->
            img.id = "notify"
            img.layoutX = 14.0
            img.layoutY = 8.0
            img.fitHeight = 24.0
            img.fitWidth = 24.0
        }

        val text = Label().also { l ->
            l.id = "telegramBtnText"
            l.layoutX = 47.0
            l.layoutY = 10.0

            config.telegram?.isNotify?.let { enabled ->
                l.text = if (enabled) "Отключить уведомления" else "Включить уведомления"
            }
        }

        config.telegram?.isNotify?.let { enabled ->
            it.id = if (enabled) "telegramNotifyOff" else "telegramNotifyOn"
        }

        it.setOnMouseClicked { actionNotify() }
        it.children.addAll(icon, text)
    }


    private val logout = getFooterRight("logout").also {
        it.layoutX = 235.0
        it.layoutY = 15.0

        it.setOnMouseClicked { logout() }
    }

    override fun init(): Double {

        if (config.telegram?.isConnected == true) {
            block.children.addAll(photo, name, username, notifyIcon, notifyText, notifyBtn, logout)
            block.prefHeight = 210.0
        } else if(config.telegram?.isWaiting == true) {
            block.children.addAll(confirmText, confirmIcon, confirmCode, confirmCodeText, confirmButton)
            block.prefHeight = 250.0
        } else {
            block.children.addAll(token, botName, tokenText, tokenButton)
            block.prefHeight = 290.0
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

    private fun logout() {
        config.telegram = null
        config.save()

        SettingsComponent().refresh()
    }

    private fun startBot(token: String, botName: String){

        if (!tokenButton.isDisable) {

            Platform.runLater {
                val loadingComponent = LoadingComponent()
                loadingComponent.initialize()

                val future = CompletableFuture.supplyAsync {
                    BotInitialize.init(token, botName)

                    if (BotInitialize.telegramBot == null || !BotInitialize.telegramBot?.isConnected!!) {
                        val component = NotifyComponent()
                        component.failure("Не удалось запустить телеграм бота, проверьте валидность данных")
                    } else {

                        config.telegram = Telegram()
                        config.telegram?.bot = TelegramBot()
                        config.telegram?.bot?.name = botName
                        config.telegram?.bot?.token = token
                        config.telegram?.bot?.code = generateCode()
                        config.telegram?.isWaiting = true

                        config.save()
                        SettingsComponent().also { it.refresh() }
                    }
                }
                future.thenApply { Platform.runLater { loadingComponent.clear() } }
            }

        }

    }

    private fun actionNotify() {
        Platform.runLater {

            val isEnabled = config.telegram?.isNotify == true

            notifyText.text = if (isEnabled) "Уведомления отключены" else "Уведомления включены"
            notifyBtn.id = if (isEnabled) "telegramNotifyOn" else "telegramNotifyOff"
            notifyBtn.setOnMouseClicked { actionNotify() }

            val label = notifyBtn.children.first { it.id == "telegramBtnText" } as Label
            label.text = if (isEnabled) "Включить уведомления" else "Отключить уведомления"

            notifyIcon.id = if (isEnabled) "red" else "green"

            logger.info("Изменение статуса получения сообщений в телеграмме на: ${!isEnabled}")

            config.telegram?.isNotify = !isEnabled
            config.save()
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