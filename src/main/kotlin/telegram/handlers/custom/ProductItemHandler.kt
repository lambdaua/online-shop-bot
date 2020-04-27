package telegram.handlers.custom

import TemplateManager
import chats.Updates
import database.User
import org.telegram.telegrambots.api.objects.Update
import telegram.Peer
import telegram.handlers.Handler

class ProductItemHandler(private val templateManager: TemplateManager) : Handler {
    override fun canHandle(update: Update): Boolean {
        return update.hasCallbackQuery() && update.callbackQuery.data.startsWith("product")
    }

    override fun handle(update: Update, peer: Peer, user: User) {
        // product;<sectionName>;<+/-><productId>
        val callbackText = Updates.messageOrCallbackText(update)
        val messageId = update.callbackQuery.message.messageId
        val split = callbackText.split(";")
        if (split.size == 1) return
        if (split.size == 2) {
            val sectionName = split[1]
            val section = templateManager.sectionByName(sectionName)
            val currentProduct = section!!.product(1) ?: return

            peer.deleteMessage(messageId)
            peer.sendMessageForProduct(section.name, currentProduct, section.totalProducts())
            return
        }
        if (split.size == 4) {
            val sectionName = split[1]
            val productNumber = split[2].toInt()
            var productId = split[3].toInt()

            val section = templateManager.sectionByName(sectionName)!!
            val totalProducts = section.totalProducts()

            if ((productNumber < 0 && productId == 0) || (productNumber > totalProducts && productId == totalProducts)) {
                return
            }

            if (productNumber < 0) productId = 0
            if (productNumber > totalProducts) productId = totalProducts

            val currentProduct = section.product(productId) ?: return
            peer.sendEditMessageForProduct(messageId, currentProduct, totalProducts, section.name)
        }
    }
}