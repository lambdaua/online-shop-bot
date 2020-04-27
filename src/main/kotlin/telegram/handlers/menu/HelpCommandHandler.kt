package telegram.handlers.menu

import chats.Updates
import database.User
import org.telegram.telegrambots.api.objects.Update
import telegram.Peer
import telegram.handlers.Handler
import utils.startsWith

class HelpCommandHandler : Handler {
    override fun canHandle(update: Update): Boolean {
        return update.hasMessage() && Updates.messageText(update).startsWith("/help")
    }

    override fun handle(update: Update, peer: Peer, user: User) {
        peer.sendSupportMessage(
            "Есть вопросы \uD83D\uDE2E? - обращайтесь в поддержку \uD83D\uDE0C"
        )
    }
}
