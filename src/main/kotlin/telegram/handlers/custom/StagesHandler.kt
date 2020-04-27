package telegram.handlers.custom

import TemplateManager
import chats.Updates
import database.User
import database.Users
import org.telegram.telegrambots.api.objects.Update
import telegram.Peer
import telegram.handlers.Handler

class StagesHandler(
    private val templateManager: TemplateManager,
    private val users: Users
) : Handler {
    override fun canHandle(update: Update): Boolean {
        return update.hasCallbackQuery() && update.callbackQuery.data.startsWith("stages")
    }

    override fun handle(update: Update, peer: Peer, user: User) {
        // stages;<section>;<stageId>;<tagName>
        val callbackText = Updates.messageOrCallbackText(update)
        val messageId = update.callbackQuery.message.messageId
        val userId = update.callbackQuery.from.id
        val split = callbackText.split(";")
        val sectionName = split[1]

        val section = templateManager.sectionByName(sectionName)!!
        if (split.size == 2) {
            val stage = section.stage().firstOrNull() ?: return

            peer.deleteMessage(messageId)
            peer.sendStageMessage(stage, sectionName)
        }
        if (split.size == 4) {
            val stageId = split[2].toInt()
            val tagName = split[3]

            users.addStage(userId.toLong(), tagName)
            val list = (user.stages + tagName).toSet().toList()

            val stage = section.nextPossibleStage(list, section.stage().first { it.id == stageId })

            if (stage == null) {
                val filteredProducts = section.filteredProducts(list)
                peer.deleteMessage(messageId)
                peer.sendFilteredProduct(sectionName, filteredProducts[0], 0, filteredProducts.size, list)
                return
            }

            peer.sendEditStageMessage(stage, sectionName, messageId)
        }
        if (split.size == 5) {
            val productIndex = split[2].toInt()
            var nextProductIndex = split[3].toInt()
            val tags = split[4].split(",")

            val filteredProducts = section.filteredProducts(tags)

            val totalProducts = filteredProducts.size
            if ((productIndex < 0 && nextProductIndex == 0) ||
                (productIndex > totalProducts && nextProductIndex == totalProducts)
            ) {
                return
            }

            if (productIndex < 0) nextProductIndex = 0
            if (productIndex > totalProducts) nextProductIndex = totalProducts

            val currentProduct = section.product(nextProductIndex) ?: return
            peer.editFilteredProduct(
                messageId,
                sectionName,
                currentProduct,
                nextProductIndex,
                totalProducts,
                tags
            )
        }
    }
}