package telegram.handlers

import database.User
import org.telegram.telegrambots.api.objects.Update
import telegram.Peer

interface Handler {
    fun canHandle(update: Update): Boolean

    fun handle(update: Update, peer: Peer, user: User)
}