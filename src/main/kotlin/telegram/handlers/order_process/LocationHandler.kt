package telegram.handlers.order_process

import database.User
import database.Users
import org.telegram.telegrambots.api.objects.Update
import telegram.Peer
import telegram.handlers.Handler

class LocationHandler(private val users: Users) : Handler {
    override fun canHandle(update: Update): Boolean {
        if (update.hasMessage()) {
            val user = users.getByTelegramId(update.message.chatId)
            return update.hasMessage() && update.message.contact != null && update.message.contact.phoneNumber.isNotEmpty() && user!!.pending!!
        }
        return false
    }

    override fun handle(update: Update, peer: Peer, user: User) {
        val userId = update.message.chatId
        val contact = update.message.contact.phoneNumber

        peer.chatBot.sendAdminAboutUser("send contact", user)

        users.addContact(userId, contact)

        peer.sendKeyboard("", listOf())
    }
}