package telegram

import TemplateManager
import chats.Updates
import configuration.AppConfiguration
import database.*
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.User
import telegram.handlers.*
import telegram.handlers.custom.PostProcessHandler
import telegram.handlers.custom.ProductItemHandler
import telegram.handlers.custom.SectionHandler
import telegram.handlers.custom.StagesHandler
import telegram.handlers.menu.FAQCommandHandler
import telegram.handlers.menu.HelpCommandHandler
import telegram.handlers.order_process.LocationHandler
import telegram.handlers.order_process.PayHandler
import telegram.handlers.order_process.PhoneHandler

class MessagesReceiver(
    private val chatBot: ChatBot,
    private val users: Users,
    private val conf: AppConfiguration,
    private val templateManager: TemplateManager,
    orders: Orders,
    private val cart: Cart,
    postStages: PostStages
) {
    private val handlers = listOf(
        StartCommandHandler(users),

        SectionHandler(templateManager),
        ProductItemHandler(templateManager),
        StagesHandler(templateManager, users),
        PostProcessHandler(templateManager, users, cart, postStages),

        //menu
        HelpCommandHandler(),
        FAQCommandHandler(),

        //order process
        PhoneHandler(users),
        PayHandler(users, orders),
        LocationHandler(users),

        //callback handlers
        OrderHandler(users, cart)
    )

    fun handle(update: Update) {
        val sender = update.sender() ?: return
        try {
            val userId = sender.id?.toLong() ?: kotlin.run {
                chatBot.sendAdmin("log if that shit happens")
                return
            }

            val passport = Passport(
                sender.firstName ?: "", sender.lastName ?: "",
                sender.userName ?: "", sender.languageCode ?: "", listOf()
            )
            val user = users.getByTelegramId(userId)
                ?: users.create(userId, passport)

            val peer = Peer(chatBot, userId.toString(), conf)

            if (user.hasBlockedBot) {
                users.didUnblockBot(userId)
            }

            if (user.pending && Updates.isCommand(update)) {
                users.deletePending(userId)
            }

            handlers.find { it.canHandle(update) }?.handle(update, peer, user) ?: run {
                chatBot.sendAdminAboutUser("unknown: ${Updates.messageOrCallbackText(update)}", user)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Update.sender(): User? {
    if (hasMessage() && message.isUserMessage) {
        return message.from
    }
    if (hasCallbackQuery()) {
        return callbackQuery.from
    }
    return null
}
