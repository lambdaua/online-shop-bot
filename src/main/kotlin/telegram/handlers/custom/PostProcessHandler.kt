package telegram.handlers.custom

import PostStage
import TemplateManager
import chats.Updates
import database.*
import org.telegram.telegrambots.api.objects.Update
import telegram.Peer
import telegram.handlers.Handler

class PostProcessHandler(
    private val manager: TemplateManager,
    private val users: Users,
    private val cart: Cart,
    private val postStages: PostStages
) : Handler {
    override fun canHandle(update: Update): Boolean {
        return update.hasCallbackQuery() && update.callbackQuery.data.startsWith("post-process")
    }

    override fun handle(update: Update, peer: Peer, user: User) {
        val callbackText = Updates.messageOrCallbackText(update)
        val messageId = update.callbackQuery.message.messageId
        val split = callbackText.split(";")

        val sectionName = split[1]
        val productId = split[2].toInt()
        val section = manager.sectionByName(sectionName)!!
        val product = section.product(productId)!!

        var postStage: PostStage? = null

        //post-process;<section>;<product>
        if (split.size == 3) {
            postStage = section.postStages().firstOrNull()!!
            peer.deleteMessage(messageId)
            peer.sendPostStageMessage(sectionName, productId, postStage)
            return
        }
        //post-process;<section>;<product>;<post-process-tag-id>
        if (split.size == 4) {
            val postProcessTagId = split[3].toInt()

            val selectedPostStage = section.postStageByTag(postProcessTagId)!!

            postStages.add(
                user.id.toString(),
                productId,
                messageId,
                selectedPostStage.name,
                selectedPostStage.tags.first { it.id == postProcessTagId }.name
            )

            postStage = section.nextPostStageFor(postProcessTagId)
        }

        if (postStage == null) {
            peer.deleteMessageKeyboard(messageId)

            val meta = postStages.getUserProductMeta(user.id.toString(), productId, messageId)
            val productMeta = meta
                .map { it.key to it.value }.toMap()

            cart.add(user.id.toString(), productId, product.name, product.price, productMeta)
            val carItems = cart.forUser(user.id.toString())

            postStages.deletePostStages(meta.map { it.id })
            peer.sendCartMessage(carItems)
            return
        }

        peer.editPostStageMessage(messageId, sectionName, productId, postStage)
    }
}