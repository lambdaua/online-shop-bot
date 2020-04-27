package telegram.handlers.order_process

import database.User
import database.Users
import org.telegram.telegrambots.api.objects.Update
import telegram.Peer
import telegram.handlers.Handler

class PhoneHandler(private val users: Users) : Handler {
    override fun canHandle(update: Update): Boolean {
        return update.hasCallbackQuery() && update.callbackQuery.data.startsWith("process-order")
    }

    override fun handle(update: Update, peer: Peer, user: User) {
//        if (user.cart.sumBy { it.quantity * it.priceInCents } == 0) {
//            peer.chatBot.sendAdminAboutUser("Нажал заказать, когда заказ 0 грн", user)
//            return
//        }
//
//        val userId = update.callbackQuery.from.id
//        peer.chatBot.sendAdminAboutUser("pressed 'order' button", user)
//
//        if (user.cart.isNotEmpty()) {
//            if (user.passport!!.tlContact!!.isEmpty()) {
//                peer.takeUserContact(
//                    "Чудово!\n\n" +
//                            "Напиши свій номер телефону щоб бариста зміг з тобою зв'язатися коли буде на місці",
//                    listOf("Поделиться номером")
//                )
//                users.setPending(userId.toLong())
//                return
//            }
//            peer.sendKeyboard(
//                "Тепер покажи нам де ти знаходишся \uD83D\uDE48 \n\n" +
//                        "Обери знизу `геопозіція` і вкажи бажане місце доставки\n", listOf()
//            )
//            users.setPending(userId.toLong())
//            return
//        }
//        peer.sendText("Для початку треба щось вибрати \uD83D\uDE44\uD83D\uDE46")
    }
}