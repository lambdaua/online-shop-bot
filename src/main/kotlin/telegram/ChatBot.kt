package telegram

import database.Passport
import database.User
import org.telegram.telegrambots.ApiContext
import org.telegram.telegrambots.api.methods.send.SendLocation
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.bots.DefaultBotOptions
import utils.Markdown

const val whiteGroupId = "360948805"
const val adminGroupID = "360948805"

class ChatBot(
    private val botToken: String,
    private val botName: String,
    private val commands: List<Command>
) : DefaultAbsSender(ApiContext.getInstance(DefaultBotOptions::class.java)) {

    class AdminBot : DefaultAbsSender(ApiContext.getInstance(DefaultBotOptions::class.java)) {
        override fun getBotToken(): String {
            return "894834584:AAFrTdnQduO6aNYQnMM9ff8lx6Tkh49OduM"
        }
    }

    val adminBot: AdminBot = AdminBot()

    val commandsSnippet: String
        get() {
            val snippet = StringBuilder()

            for (command in commands) {
                snippet.append("\n")
                snippet.append("/").append(command.title)
                snippet.append(" - ").append(command.description)
            }
            return snippet.toString()
        }

    override fun getBotToken(): String {
        return botToken
    }

    fun sendAdmin(text: String) {
        try {
            val sendMessage = SendMessage(adminGroupID, Markdown.escape(text))
            sendMessage.enableMarkdown(true)
            sendMessage.disableWebPagePreview()

            adminBot.execute(sendMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendAdminAboutUser(text: String, from: User) {
        try {
            val message = Markdown.escape(from.passport!!.usernameOrIdLink(from)) + " " + Markdown.escape(text)

            val sendMessage = SendMessage(adminGroupID, message)
            sendMessage.enableMarkdown(true)
            sendMessage.disableWebPagePreview()

            adminBot.execute(sendMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendOrderToWhiteAdminGroup(user: User, orderMessage: String, amountPaid: String) {
        val messageBuilder = StringBuilder()
        val message = Markdown.escape(user.passport!!.usernameOrIdLink(user))
        messageBuilder.append("НОВЫЙ ЗАКАЗ\n\nСсылка на телеграм -> $message")
        messageBuilder.append("\nНомер телефона -> ${user.passport.tlContact ?: ""}")
        messageBuilder.append("\nЗаказ:\n $orderMessage")
        messageBuilder.append("\nОплатил: $amountPaid грн")

        val sendMessage = SendMessage(whiteGroupId, messageBuilder.toString())
            .enableMarkdown(true)
            .disableWebPagePreview()

        execute(sendMessage).messageId
        val sendLocation =
            SendLocation(user.location!!.latitude, user.location.longitude).setChatId(whiteGroupId)
        execute(sendLocation)
    }


    data class Command(val title: String, val description: String)
}

fun Number.telegramLink(text: String): String {
    return "[$text](tg://user?id=$this)"
}

fun Passport.usernameOrIdLink(user: User): String {
    if (tlUsername.isEmpty()) return user.userId!!.telegramLink(this.tlFirstName)
    return "@" + this.tlUsername
}
