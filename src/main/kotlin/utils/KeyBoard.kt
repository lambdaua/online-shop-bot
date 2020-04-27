package utils

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendPhoto
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow

class Keyboard(private val sendMessage: SendMessage) {
    private val rows = mutableListOf<ButtonsRow>()

    fun nextRow(): ButtonsRow {
        val newRow = ButtonsRow(this)
        rows.add(newRow)
        return newRow
    }

    fun build(): SendMessage {
        val markup = InlineKeyboardMarkup()
        markup.keyboard = rows.map { it.buttons }

        sendMessage.replyMarkup = markup
        return sendMessage
    }

    fun withButtons(buttons: List<InlineKeyboardButton>): SendMessage {
        if (buttons.isEmpty()) return sendMessage

        sendMessage.replyMarkup = InlineKeyboardMarkup()
            .setKeyboard(listOf(buttons))

        return sendMessage
    }
}

class EditKeyboard(private val editMessageText: EditMessageText) {
    private val rows = mutableListOf<EditButtonsRow>()

    fun nextRow(): EditButtonsRow {
        val newRow = EditButtonsRow(this)
        rows.add(newRow)
        return newRow
    }

    fun build(): EditMessageText {
        val markup = InlineKeyboardMarkup()
        markup.keyboard = rows.map { it.buttons }

        editMessageText.replyMarkup = markup
        return editMessageText
    }

    fun withButtons(buttons: List<InlineKeyboardButton>): EditMessageText {
        if (buttons.isEmpty()) return editMessageText

        editMessageText.replyMarkup = InlineKeyboardMarkup()
            .setKeyboard(listOf(buttons))

        return editMessageText
    }
}

class EditMediaKeyboard(private val editMessageMedia: EditMessageMedia) {
    private val rows = mutableListOf<EditMediaButonsRow>()

    fun nextRow(): EditMediaButonsRow {
        val newRow = EditMediaButonsRow(this)
        rows.add(newRow)
        return newRow
    }

    fun build(): EditMessageMedia {
        val markup = InlineKeyboardMarkup()
        markup.keyboard = rows.map { it.buttons }

        editMessageMedia.replyMarkup = markup
        return editMessageMedia
    }

    fun withButtons(buttons: List<InlineKeyboardButton>): EditMessageMedia {
        if (buttons.isEmpty()) return editMessageMedia

        editMessageMedia.replyMarkup = InlineKeyboardMarkup()
            .setKeyboard(listOf(buttons))

        return editMessageMedia
    }
}

class PhotoKeyboard(private val sendPhoto: SendPhoto) {
    private val rows = mutableListOf<PhotoButtonsRow>()

    fun nextRow(): PhotoButtonsRow {
        val newRow = PhotoButtonsRow(this)
        rows.add(newRow)
        return newRow
    }

    fun build(): SendPhoto {
        val markup = InlineKeyboardMarkup()
        markup.keyboard = rows.map { it.buttons }

        sendPhoto.replyMarkup = markup
        return sendPhoto
    }

    fun withButtons(buttons: List<InlineKeyboardButton>): SendPhoto {
        if (buttons.isEmpty()) return sendPhoto

        sendPhoto.replyMarkup = InlineKeyboardMarkup()
            .setKeyboard(listOf(buttons))

        return sendPhoto
    }
}

class ButtonsRow(private val keyboard: Keyboard) {
    val buttons = mutableListOf<InlineKeyboardButton>()

    fun linkButton(text: String, link: String): ButtonsRow {
        val button = InlineKeyboardButton()
            .setText(text)
            .setUrl(link)

        buttons.add(button)
        return this
    }

    fun callbackButton(text: String, callback: String): ButtonsRow {
        val button = InlineKeyboardButton()
            .setText(text)
            .setCallbackData(callback)

        buttons.add(button)
        return this
    }

    fun nextRow(): ButtonsRow {
        return keyboard.nextRow()
    }

    fun end(): SendMessage {
        return keyboard.build()
    }
}

class PhotoButtonsRow(private val keyboard: PhotoKeyboard) {
    val buttons = mutableListOf<InlineKeyboardButton>()

    fun linkButton(text: String, link: String): PhotoButtonsRow {
        val button = InlineKeyboardButton()
            .setText(text)
            .setUrl(link)

        buttons.add(button)
        return this
    }

    fun callbackButton(text: String, callback: String): PhotoButtonsRow {
        val button = InlineKeyboardButton()
            .setText(text)
            .setCallbackData(callback)

        buttons.add(button)
        return this
    }

    fun nextRow(): PhotoButtonsRow {
        return keyboard.nextRow()
    }

    fun end(): SendPhoto {
        return keyboard.build()
    }
}

class EditButtonsRow(private val keyboard: EditKeyboard) {
    val buttons = mutableListOf<InlineKeyboardButton>()

    fun linkButton(text: String, link: String): EditButtonsRow {
        val button = InlineKeyboardButton()
            .setText(text)
            .setUrl(link)

        buttons.add(button)
        return this
    }

    fun callbackButton(text: String, callback: String): EditButtonsRow {
        val button = InlineKeyboardButton()
            .setText(text)
            .setCallbackData(callback)

        buttons.add(button)
        return this
    }

    fun nextRow(): EditButtonsRow {
        return keyboard.nextRow()
    }

    fun end(): EditMessageText {
        return keyboard.build()
    }
}

class EditMediaButonsRow(private val keyboard: EditMediaKeyboard) {
    val buttons = mutableListOf<InlineKeyboardButton>()

    fun linkButton(text: String, link: String): EditMediaButonsRow {
        val button = InlineKeyboardButton()
            .setText(text)
            .setUrl(link)

        buttons.add(button)
        return this
    }

    fun callbackButton(text: String, callback: String): EditMediaButonsRow {
        val button = InlineKeyboardButton()
            .setText(text)
            .setCallbackData(callback)

        buttons.add(button)
        return this
    }

    fun nextRow(): EditMediaButonsRow {
        return keyboard.nextRow()
    }

    fun end(): EditMessageMedia {
        return keyboard.build()
    }
}

fun SendMessage.inlineKeyboard(): Keyboard {
    return Keyboard(this)
}

fun EditMessageText.inlineKeyboard(): EditKeyboard {
    return EditKeyboard(this)
}

fun SendPhoto.inlineKeyboard(): PhotoKeyboard {
    return PhotoKeyboard(this)
}

fun EditMessageMedia.inlineKeyboard(): EditMediaKeyboard {
    return EditMediaKeyboard(this)
}

fun SendMessage.linkButton(text: String, url: String): SendMessage {
    val button = InlineKeyboardButton()
    button.text = text
    button.url = url

    val markup = InlineKeyboardMarkup()
    markup.keyboard = listOf(listOf(button))

    this.replyMarkup = markup
    return this
}

fun SendMessage.enableNotification(should: Boolean): SendMessage {
    return if (should) enableNotification() else disableNotification()
}

fun KeyboardRow.with(vararg items: String): KeyboardRow {
    val row = this
    for (item in items) {
        row.add(item)
    }
    return row
}

fun Int.telegramLink(text: String): String {
    return "[$text](tg://user?id=$this)"
}

fun Int.usernameIfExist(username: String, firstName: String, lastName: String): String {
    return when {
        !username.isEmpty() -> "@${Markdown.escape(username)}"
        !firstName.isEmpty() -> "[$firstName $lastName](tg://user?id=$this)"
        else -> "[$lastName](tg://user?id=$this)"
    }
}

fun String.startsWith(vararg vals: String): Boolean {
    return vals.firstOrNull { this.startsWith(it, false) } != null
}

fun String.insta(): String {
    return "[@$this](https://www.instagram.com/$this)"
}

fun String.instaMentionHtml(): String {
    return "<a href=\"https://www.instagram.com/$this\">/_@$this</a>"
}

fun String.usernameCommand(userId: Long): String {
    val s = if (this.contains(".")) "$userId $this" else "@$this"
    return "/_$s"
}