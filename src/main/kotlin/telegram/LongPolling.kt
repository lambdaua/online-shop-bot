package telegram

import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot

class LongPolling(
    private val username: String,
    private val token: String,
    private val messagesReceiver: MessagesReceiver
) : TelegramLongPollingBot() {

    override fun onUpdateReceived(update: Update) {
        try {
            messagesReceiver.handle(update)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getBotUsername(): String {
        return username
    }

    override fun getBotToken(): String {
        return token
    }
}
