package telegram.handlers

import database.*
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.api.objects.Update
import telegram.Peer
import telegram.generateCartMessage
import utils.inlineKeyboard

class OrderHandler(
    private val users: Users,
    private val cart: Cart
) : Handler {
    override fun canHandle(update: Update): Boolean {
        return update.hasMessage() && update.message.text == "/order"
    }

    override fun handle(update: Update, peer: Peer, user: User) {
        val carItems = cart.forUser(user.id.toString())
        peer.sendCartMessage(carItems)
    }
}