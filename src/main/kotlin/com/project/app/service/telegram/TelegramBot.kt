package com.project.app.service.telegram

import com.project.app.models.ConfigModel
import com.project.app.models.TelegramUser
import com.project.app.ui.component.notify.NotifyComponent
import com.project.app.ui.component.settings.SettingsComponent
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

private const val EMOJI = "\uD83E\uDD16"
private const val TELEGRAM_PHOTO_URL = "https://api.telegram.org/file/bot%s/%s"

class TelegramBot(
    private val token: String,
    private val botName: String
): TelegramLongPollingBot (token) {

    var isConnected: Boolean = false

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update?) {

        if (update != null && update.hasMessage()) {

            val config = ConfigModel().init()

            val message = update.message

            val text = message.text
            val user = message.from

            if (config.telegram != null && config.telegram?.isWaiting == true) {

                if (text == config.telegram?.bot?.code || text == "/start ${config.telegram!!.bot!!.code!!}") {

                    var photo: String? = null

                    val photos = execute(GetUserProfilePhotos(user.id))
                    if (photos != null && photos.photos.isNotEmpty()) {
                        val firstPhoto = photos.photos[0][0]
                        val filePath = execute(GetFile(firstPhoto.fileId)).filePath
                        photo = String.format(TELEGRAM_PHOTO_URL, token, filePath)
                    }

                    config.telegram?.isWaiting = false
                    config.telegram?.isConnected = true
                    config.telegram?.user = TelegramUser(
                        name = (user.firstName + " " + user.lastName).trim(),
                        username = user.userName,
                        chatId = message.chatId,
                        photo = photo
                    )
                    config.save()

                    SettingsComponent().refresh()

                    val notifyComponent = NotifyComponent()
                    notifyComponent.success("Телеграм был успешно подключен, теперь вам будут приходить различные уведомления, информативные и ожидающие вашего подтверждения!")

                    reply(message.chatId, message.messageId, "Вы успешно авторизовались в приложении!")
                }

            }

        }
    }

    private fun reply(chatId: Long, messageId: Int, text: String) {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = "$EMOJI $text"
        message.replyToMessageId = messageId

        execute(message)
    }
}