package telegram.handlers.custom

import TemplateManager
import chats.Updates
import database.User
import org.telegram.telegrambots.api.objects.Update
import telegram.Peer
import telegram.categoriesMessage
import telegram.handlers.Handler

class SectionHandler(private val templateManager: TemplateManager) : Handler {
    override fun canHandle(update: Update): Boolean {
        return update.hasCallbackQuery() && update.callbackQuery.data.startsWith("sections")
    }

    override fun handle(update: Update, peer: Peer, user: User) {
        val callbackText = Updates.messageOrCallbackText(update)
        val messageId = update.callbackQuery.message.messageId
        val split = callbackText.split(";")

        val sectionNames = templateManager.sectionNames()

        if (split.size == 2) peer.deleteMessage(messageId)

        peer.sendSectionsMessage(categoriesMessage, sectionNames)
    }
}