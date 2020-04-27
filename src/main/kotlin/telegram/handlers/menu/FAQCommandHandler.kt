package telegram.handlers.menu

import chats.Updates
import database.User
import org.telegram.telegrambots.api.objects.Update
import telegram.Peer
import telegram.faq
import telegram.handlers.Handler

class FAQCommandHandler : Handler {
    override fun canHandle(update: Update): Boolean {
        return update.hasMessage() && Updates.messageText(update).startsWith("/faq")
    }

    override fun handle(update: Update, peer: Peer, user: User) {
        peer.sendText(faq)
    }
}