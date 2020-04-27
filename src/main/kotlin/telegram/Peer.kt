package telegram

import Data
import PostStage
import Stage
import configuration.AppConfiguration
import database.CartGroupingKey
import org.telegram.telegrambots.api.methods.ParseMode
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendPhoto
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.exceptions.TelegramApiException
import utils.EditMessageMedia
import utils.inlineKeyboard

const val categoriesMessage = "Давай сперва выберем категорию \uD83E\uDD14"

const val faq = ""

class Peer(val chatBot: ChatBot, val chatId: String, val configuration: AppConfiguration) {
    @Throws(TelegramApiException::class)
    fun sendText(text: String, markdown: Boolean = true) {
        val sendMessage = SendMessage()
            .setChatId(chatId)
            .setText(text)
            .disableWebPagePreview()

        if (markdown) sendMessage.setParseMode(ParseMode.MARKDOWN)

        chatBot.execute(sendMessage)
    }

    fun sendSupportMessage(message: String) {
        chatBot.execute(
            SendMessage(chatId, message)
                .enableMarkdown(true)
                .disableWebPagePreview()
                .disableNotification()
                .inlineKeyboard()
                .nextRow()
                .linkButton("Підтримка \uD83D\uDE0E", "https://t.me/Anna_Mariiaa")
                .end()
        )
    }

    fun takeUserContact(message: String, buttons: List<String>) {
        val keyboard = buttons.map {
            val row = KeyboardRow()
            row.add(KeyboardButton().setRequestContact(true).setText(it))
            row
        }

        chatBot.execute(
            SendMessage(chatId, message)
                .enableMarkdown(true)
                .disableWebPagePreview()
                .disableNotification()
                .setReplyMarkup(ReplyKeyboardMarkup().setKeyboard(keyboard).setResizeKeyboard(true))
        )
    }

    fun sendPaymentMessage(message: String, orderId: String) {
        chatBot.execute(
            SendMessage(chatId, message)
                .enableMarkdown(true)
                .disableWebPagePreview()
                .disableNotification()
                .inlineKeyboard()
                .nextRow()
                .linkButton("Оплатить \uD83D\uDD25", "${configuration.serviceUrl}/wfp/pay-now?payload=$orderId")
                .end()
        )
    }

    fun sendKeyboard(text: String, buttons: List<String>) {
        val keyboard = buttons.map {
            val row = KeyboardRow()
            row.add(KeyboardButton().setText(it))
            row
        }


        val markup = if (keyboard.isEmpty()) {
            ReplyKeyboardRemove()
        } else {
            ReplyKeyboardMarkup().setKeyboard(keyboard).setResizeKeyboard(true)
        }

        chatBot.execute(
            SendMessage()
                .setChatId(chatId)
                .setText(text)
                .setReplyMarkup(markup)
        )
    }

    fun sendSectionsMessage(cartMessage: String, sectionNames: List<String>) {
        val sendEditMessage = SendMessage()
            .setChatId(chatId)
            .setText(cartMessage)
            .enableMarkdown(true)
            .disableWebPagePreview()
            .inlineKeyboard()
            .nextRow()

        sectionNames.forEach {
            sendEditMessage
                .nextRow()
                .callbackButton(it, "product;$it")
        }

        sendEditMessage
            .nextRow()
            .callbackButton("Назад", "order")

        chatBot.execute(sendEditMessage.end())
    }

    fun deleteMessage(messageId: Int) {
        chatBot.execute(
            DeleteMessage()
                .setChatId(chatId)
                .setMessageId(messageId)
        )
    }

    fun sendStageMessage(stage: Stage, sectionName: String) {
        val sendStage = SendMessage()
            .setChatId(chatId)
            .setText(stage.text)
            .enableMarkdown(true)
            .disableWebPagePreview()
            .inlineKeyboard()
            .nextRow()

        stage.tags.forEach {
            sendStage.nextRow().callbackButton(it, "stages;$sectionName;${stage.id};$it")
        }

        chatBot.execute(sendStage.end())
    }

    fun sendEditStageMessage(stage: Stage, sectionName: String, messageId: Int) {
        val sendEditStage = EditMessageText()
            .setChatId(chatId)
            .setMessageId(messageId)
            .setText(stage.text)
            .enableMarkdown(true)
            .disableWebPagePreview()
            .inlineKeyboard()
            .nextRow()

        stage.tags.forEach {
            sendEditStage.nextRow().callbackButton(it, "stages;$sectionName;${stage.id};$it")
        }

        chatBot.execute(sendEditStage.end())

    }

    fun sendFilteredProduct(
        sectionName: String,
        product: Data,
        productIndex: Int,
        totalProducts: Int,
        tags: List<String>
    ) {
        val sendPhoto =
            SendPhoto()
                .setChatId(chatId)
                .setPhoto(product.imageUrl)
                .setCaption("${product.name}\n\n" + product.description + "\n\nЦена: ${product.price} грн.")
                .disableNotification()
                .inlineKeyboard()
                .nextRow()

        // Navigation row
        val tagsString = tags.joinToString(",")
        sendPhoto.callbackButton("⏪", "stages;$sectionName;$productIndex;${productIndex - 5};$tagsString")
            .callbackButton("◀️", "stages;$sectionName;$productIndex;${productIndex - 1};$tagsString")
            .callbackButton("${productIndex + 1}/$totalProducts", "text")
            .callbackButton("▶️", "stages;$sectionName;$productIndex;${productIndex + 1};$tagsString")
            .callbackButton("⏩", "stages;$sectionName;$productIndex;${productIndex + 5};$tagsString")
            .nextRow()
            .callbackButton("Добавить", "post-process;$sectionName;${product.id}")
            .nextRow()
            .callbackButton("Сбросить фильтры", "product;$sectionName;edit")

        chatBot.sendPhoto(sendPhoto.end())
    }

    fun editFilteredProduct(
        messageId: Int,
        sectionName: String,
        product: Data,
        productIndex: Int,
        totalProducts: Int,
        tags: List<String>
    ) {
        val editFilteredProduct =
            EditMessageMedia()
                .setChatId(chatId)
                .setMessageId(messageId)
                .setMedia(
                    InputMediaPhoto()
                        .setMedia(product.imageUrl)
                        .setCaption("${product.name}\n\n" + product.description + "\n\nЦена: ${product.price} грн.")
                )
                .inlineKeyboard()
                .nextRow()

        // Navigation row
        val tagsString = tags.joinToString(",")
        editFilteredProduct.callbackButton("⏪", "stages;$sectionName;$productIndex;${productIndex - 5};$tagsString")
            .callbackButton("◀️", "stages;$sectionName;$productIndex;${productIndex - 1};$tagsString")
            .callbackButton("${productIndex + 1}/$totalProducts", "text")
            .callbackButton("▶️", "stages;$sectionName;$productIndex;${productIndex + 1};$tagsString")
            .callbackButton("⏩", "stages;$sectionName;$productIndex;${productIndex + 5};$tagsString")
            .nextRow()
            .callbackButton("Добавить", "post-process;$sectionName;${product.id}")
            .nextRow()
            .callbackButton("Сбросить фильтры", "product;$sectionName;edit")

        chatBot.execute(editFilteredProduct.end())
    }

    fun sendMessageForProduct(sectionName: String, product: Data, totalProducts: Int) {
        val sendPhoto =
            SendPhoto()
                .setChatId(chatId)
                .setPhoto(product.imageUrl)
                .setCaption("${product.name}\n\n" + product.description + "\n\nЦена: ${product.price} грн.")
                .disableNotification()
                .inlineKeyboard()
                .nextRow()

        // Navigation row
        val productId = product.id!!.toInt()
        sendPhoto.callbackButton("⏪", "product;$sectionName;${product.id};${productId - 5}")
            .callbackButton("◀️", "product;$sectionName;${product.id};${productId - 1}")
            .callbackButton("${product.id}/$totalProducts", "text")
            .callbackButton("▶️", "product;$sectionName;${product.id};${productId + 1}")
            .callbackButton("⏩", "product;$sectionName;${product.id};${productId + 5}")
            .nextRow()
            .callbackButton("Отфильтровать", "stages;$sectionName")
            .nextRow()
            .callbackButton("Добавить", "post-process;$sectionName;${product.id}")
            .nextRow()
            .callbackButton("Назад", "sections;media")

        chatBot.sendPhoto(sendPhoto.end())
    }

    fun sendEditMessageForProduct(messageId: Int, product: Data, totalProducts: Int, sectionName: String) {
        val sendEditMessageWithPhoto =
            EditMessageMedia()
                .setChatId(chatId)
                .setMessageId(messageId)
                .setMedia(
                    InputMediaPhoto()
                        .setMedia(product.imageUrl)
                        .setCaption("${product.name}\n\n" + product.description + "\n\nЦена: ${product.price} грн.")
                )
                .inlineKeyboard()
                .nextRow()

        val productId = product.id!!.toInt()
        sendEditMessageWithPhoto.callbackButton("⏪", "product;$sectionName;${product.id};${productId - 5}")
            .callbackButton("◀️", "product;$sectionName;${product.id};${productId - 1}")
            .callbackButton("${product.id}/$totalProducts", "text")
            .callbackButton("▶️", "product;$sectionName;${product.id};${productId + 1}")
            .callbackButton("⏩", "product;$sectionName;${product.id};${productId + 5}")
            .nextRow()
            .callbackButton("Помочь с выбором", "stages;$sectionName")
            .nextRow()
            .callbackButton("Добавить", "post-process;$sectionName;${product.id}")
            .nextRow()
            .callbackButton("Назад", "sections;media")

        chatBot.execute(sendEditMessageWithPhoto.end())


    }

    fun sendPostStageMessage(sectionName: String, productId: Int, postStage: PostStage) {
        val sendMessage = SendMessage()
            .setChatId(chatId)
            .setText(postStage.name)
            .enableMarkdown(true)
            .disableWebPagePreview()
            .inlineKeyboard()
            .nextRow()

        postStage.tags.chunked(2).forEach {
            val nextRow = sendMessage.nextRow()
            it.forEach {
                nextRow.callbackButton(it.name, "post-process;$sectionName;${productId};${it.id}")
            }
        }

        chatBot.execute(sendMessage.end())
    }

    fun editPostStageMessage(messageId: Int, sectionName: String, productId: Int, postStage: PostStage) {
        val editMessage = EditMessageText()
            .setChatId(chatId)
            .setText(postStage.name)
            .setMessageId(messageId)
            .enableMarkdown(true)
            .disableWebPagePreview()
            .inlineKeyboard()
            .nextRow()

        postStage.tags.chunked(2).forEach {
            val nextRow = editMessage.nextRow()
            it.forEach {
                nextRow.callbackButton(it.name, "post-process;$sectionName;${productId};${it.id}")
            }
        }

        chatBot.execute(editMessage.end())
    }

    fun deleteMessageKeyboard(messageId: Int) {
        chatBot.execute(
            EditMessageReplyMarkup()
                .setChatId(chatId)
                .setMessageId(messageId)
        )
    }

    fun sendCartMessage(cart: Map<CartGroupingKey, Int>) {
        val cartMessage = generateCartMessage(cart)
        val sendMessage = SendMessage(chatId, cartMessage)
            .enableMarkdown(true)
            .disableWebPagePreview()
            .disableNotification()
            .inlineKeyboard()
            .nextRow()
            .callbackButton("Добавить товар", "sections")
            .nextRow()

        if (cart.isNotEmpty()) {
            sendMessage.callbackButton("Заказать", "order")
        }

        chatBot.execute(sendMessage.end())
    }
}

data class Button(val text: String, val callback: String, val url: Boolean = false)