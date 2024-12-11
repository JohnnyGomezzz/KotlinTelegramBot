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
    val trainer = LearnWordsTrainer()
    var lastUpdateId = 0L

    while (true) {
        Thread.sleep(2000)
        val updates = service.getUpdates(lastUpdateId).result
        val firstUpdate = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1

        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val receivedText = firstUpdate.message?.text
        val receivedData = firstUpdate.callbackQuery?.data

        if (receivedText == "/start".lowercase() && chatId != null) service.sendMenu(chatId)
        if (receivedData == STATISTICS_CLICKED && chatId != null) {
            val statistics = trainer.getStatistics()
            service.sendMessage(
                chatId,
                String.format(
                    "Выучено %d из %d слов | %d%%",
                    statistics.learnedWords,
                    statistics.totalCount,
                    statistics.percent
                )
            )
        }
        if (receivedData == LEARN_WORDS_CLICKED && chatId != null) checkNextQuestionAndSend(trainer, service, chatId)
        if (receivedData == BACK_TO_MENU && chatId != null) service.sendMenu(chatId)
        if (receivedData?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true && chatId != null) {
            val dataIndex = receivedData.substringAfter("_").toInt()
            if (trainer.checkAnswer(dataIndex)) {
                service.sendMessage(
                    chatId,
                    "Правильно!")
            } else {
                service.sendMessage(
                    chatId,
                    "Неправильно! ${trainer.question?.correctAnswer?.original} - это ${trainer.question?.correctAnswer?.translate}"
                )
            }
            checkNextQuestionAndSend(trainer, service, chatId)
        }
    }
}

fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, service: TelegramBotService, chatId: Long) {
    val question = trainer.getNextQuestion()
    if (question == null) {
        service.sendMessage(
            chatId,
            "Все слова в базе выучены!"
        )
        service.sendMenu(chatId)
    } else service.sendQuestion(chatId, question)
}