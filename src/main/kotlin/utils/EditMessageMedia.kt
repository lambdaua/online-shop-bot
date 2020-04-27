package utils

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.api.methods.PartialBotApiMethod
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.media.InputMedia
import org.telegram.telegrambots.api.objects.replykeyboard.ApiResponse
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.exceptions.TelegramApiRequestException
import org.telegram.telegrambots.exceptions.TelegramApiValidationException
import java.io.IOException
import java.io.Serializable


class EditMessageMedia : BotApiMethod<Serializable?>() {


    /**
     * Required if inline_message_id is not specified. Unique identifier for the chat to send the
     * message to (Or username for channels)
     */
    @JsonProperty(CHATID_FIELD)
    var chatId: String? = null

    /**
     * Required if inline_message_id is not specified. Unique identifier of the sent message
     */
    @JsonProperty(MESSAGEID_FIELD)
    var messageId: Int? = null

    /**
     * Required if chat_id and message_id are not specified. Identifier of the inline message
     */
    @JsonProperty(INLINE_MESSAGE_ID_FIELD)
    var inlineMessageId: String? = null

    @JsonProperty(MEDIA_FIELD)
    var media: InputMedia<*>? = null

    @JsonProperty(REPLYMARKUP_FIELD)
    var replyMarkup ///< Optional. A JSON-serialized object for an inline keyboard.
            : InlineKeyboardMarkup? = null

    fun setChatId(chatId: String?): EditMessageMedia {
        this.chatId = chatId
        return this
    }

    fun setMessageId(messageId: Int?): EditMessageMedia {
        this.messageId = messageId
        return this
    }

    fun setInlineMessageId(inlineMessageId: String?): EditMessageMedia {
        this.inlineMessageId = inlineMessageId
        return this
    }

    fun setMedia(media: InputMedia<*>?): EditMessageMedia {
        this.media = media
        return this
    }

    fun setReplyMarkup(replyMarkup: InlineKeyboardMarkup?): EditMessageMedia {
        this.replyMarkup = replyMarkup
        return this
    }

    override fun getMethod(): String {
        return PATH
    }

    @Throws(TelegramApiRequestException::class)
    override fun deserializeResponse(answer: String): Serializable {
        return try {
            val result = PartialBotApiMethod.OBJECT_MAPPER.readValue<ApiResponse<Message>>(answer,
                object : TypeReference<ApiResponse<Message?>?>() {})
            if (result.ok) {
                result.result
            } else {
                throw TelegramApiRequestException("Error editing message media", result)
            }
        } catch (e: IOException) {
            try {
                val result =
                    PartialBotApiMethod.OBJECT_MAPPER.readValue<ApiResponse<Boolean>>(answer,
                        object : TypeReference<ApiResponse<Boolean?>?>() {})
                if (result.ok) {
                    result.result
                } else {
                    throw TelegramApiRequestException("Error editing message media", result)
                }
            } catch (e2: IOException) {
                throw TelegramApiRequestException("Unable to deserialize response", e)
            }
        }
    }

    @Throws(TelegramApiValidationException::class)
    override fun validate() {
        if (inlineMessageId == null) {
            if (chatId == null) {
                throw TelegramApiValidationException(
                    "ChatId parameter can't be empty if inlineMessageId is not present",
                    this
                )
            }
            if (messageId == null) {
                throw TelegramApiValidationException(
                    "MessageId parameter can't be empty if inlineMessageId is not present",
                    this
                )
            }
        } else {
            if (chatId != null) {
                throw TelegramApiValidationException(
                    "ChatId parameter must be empty if inlineMessageId is provided",
                    this
                )
            }
            if (messageId != null) {
                throw TelegramApiValidationException(
                    "MessageId parameter must be empty if inlineMessageId is provided",
                    this
                )
            }
        }
        if (replyMarkup != null) {
            replyMarkup!!.validate()
        }
    }

    override fun toString(): String {
        return "EditMessageMedia{" +
                "chatId='" + chatId + '\'' +
                ", messageId=" + messageId +
                ", inlineMessageId='" + inlineMessageId + '\'' +
                ", media='" + media + '\'' +
                ", replyMarkup=" + replyMarkup +
                '}'
    }

    companion object {
        const val PATH = "editmessagemedia"
        private const val CHATID_FIELD = "chat_id"
        private const val MESSAGEID_FIELD = "message_id"
        private const val INLINE_MESSAGE_ID_FIELD = "inline_message_id"
        private const val MEDIA_FIELD = "media"
        private const val REPLYMARKUP_FIELD = "reply_markup"
    }
}