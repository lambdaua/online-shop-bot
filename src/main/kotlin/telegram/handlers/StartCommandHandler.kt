package telegram.handlers

import chats.Updates
import database.User
import database.Users
import org.telegram.telegrambots.api.objects.Update
import telegram.Peer
import telegram.handlers.Handler
import utils.startsWith

class StartCommandHandler(val users: Users) : Handler {

    override fun canHandle(update: Update): Boolean {
        return update.hasMessage() && Updates.messageText(update).startsWith("/start")
    }

    override fun handle(update: Update, peer: Peer, user: User) {
        if (!user.hasBlockedBot) {
            peer.sendText("Привет!")
            peer.chatBot.sendAdminAboutUser("started", user)
        }
    }
}