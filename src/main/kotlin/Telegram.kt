package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String? = null,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

fun main(args: Array<String>) {
    val service = TelegramBotService(args[0])
    var lastUpdateId = 0L
    val trainers = HashMap<Long, LearnWordsTrainer>()

    while (true) {
        Thread.sleep(2000)
        val updates = service.getUpdates(lastUpdateId).result
        if (updates.isEmpty()) continue
        val sortedUpdates = updates.sortedBy { it.updateId }
        sortedUpdates.forEach { service.handleUpdate(it, trainers) }
        lastUpdateId = sortedUpdates.last().updateId + 1
    }
}
