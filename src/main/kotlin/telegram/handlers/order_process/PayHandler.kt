package telegram.handlers.order_process

import database.Order
import database.Orders
import database.User
import database.Users
import org.telegram.telegrambots.api.objects.Update
import telegram.Peer
import telegram.handlers.Handler

class PayHandler(val users: Users, val orders: Orders) : Handler {
    override fun canHandle(update: Update): Boolean {
        if (update.hasMessage()) {
            val user = users.getByTelegramId(update.message.chatId)
            return update.message.hasLocation() && user!!.pending
        }
        return false
    }

    override fun handle(update: Update, peer: Peer, user: User) {
        val userId = update.message.chatId

        peer.chatBot.sendAdminAboutUser("send location", user)
        users.addLocation(userId, update.message.location)

        peer.sendPaymentMessage("", user.id.toString())

        users.deletePending(userId)
    }
}