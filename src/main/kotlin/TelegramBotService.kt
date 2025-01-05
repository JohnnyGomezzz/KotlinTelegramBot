package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val TELEGRAM_URL = "https://api.telegram.org/bot"
const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val STATISTICS_CLICKED = "statistics_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
const val BACK_TO_MENU = "back_to_menu"
const val RESET_CLICKED = "reset_clicked"

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("text")
    val text: String,
    @SerialName("callback_data")
    val callbackData: String,
)

class TelegramBotService(
    private val botToken: String,
) {
    private val client: HttpClient = HttpClient.newBuilder().build()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getUpdates(updateId: Long): Response {
        val urlGetUpdates = "$TELEGRAM_URL$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: IOException) {
            return Response(listOf(Update(0L, null, null)))
        }

        val responseString: String = response.body()
        println(responseString)
        return json.decodeFromString(responseString)
    }

    private fun sendMessage(chatId: Long, message: String): String {
        val encoded = URLEncoder.encode(message, StandardCharsets.UTF_8)
        val urlSendMessage = "$TELEGRAM_URL$botToken/sendMessage?chat_id=$chatId&text=$encoded"

        val requestBody = SendMessageRequest(chatId, message)
        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    private fun sendMenu(chatId: Long): String {
        val urlSendMessage = "$TELEGRAM_URL$botToken/sendMessage"

        val requestBody = SendMessageRequest(
            chatId, "Главное меню", ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(
                            text = "Учить слова",
                            callbackData = LEARN_WORDS_CLICKED,
                        ),
                        InlineKeyboard(
                            text = "Статистика",
                            callbackData = STATISTICS_CLICKED,
                        ),
                    ),
                    listOf(
                        InlineKeyboard(
                            text = "Сбросить прогресс",
                            callbackData = RESET_CLICKED,
                        ),
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    private fun sendQuestion(chatId: Long, question: Question): String {
        val urlSendMessage = "$TELEGRAM_URL$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId, question.correctAnswer.original, ReplyMarkup(
                (question.variants.mapIndexed { index, word ->
                    InlineKeyboard(
                        text = word.translate,
                        callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index"
                    )
                } + InlineKeyboard(
                    text = "В главное меню",
                    callbackData = BACK_TO_MENU
                )).chunked(2)
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun handleUpdate(update: Update, trainers: HashMap<Long, LearnWordsTrainer>) {
        val userName = update.message?.chat?.userName
        val chatId = update.message?.chat?.id ?: update.callbackQuery?.message?.chat?.id ?: return
        val receivedText = update.message?.text
        val receivedData = update.callbackQuery?.data

        val dictionary = DatabaseUserDictionary(chatId)
        if (userName != null) {
            dictionary.loadDictionary(userName)
        }

        val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer(chatId) }

        if (receivedText == "/start".lowercase()) sendMenu(chatId)
        if (receivedData == STATISTICS_CLICKED) {
            val statistics = trainer.getStatistics()
            sendMessage(
                chatId,
                String.format(
                    "Выучено %d из %d слов | %d%%",
                    statistics.learnedWords,
                    statistics.totalCount,
                    statistics.percent
                )
            )
            sendMenu(chatId)
        }
        if (receivedData == LEARN_WORDS_CLICKED) checkNextQuestionAndSend(trainer, chatId)
        if (receivedData == BACK_TO_MENU) sendMenu(chatId)
        if (receivedData?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
            val dataIndex = receivedData.substringAfter("_").toInt()
            if (trainer.checkAnswer(dataIndex)) {
                sendMessage(
                    chatId,
                    "Правильно!"
                )
            } else {
                sendMessage(
                    chatId,
                    "Неправильно! ${trainer.question?.correctAnswer?.original} - это ${trainer.question?.correctAnswer?.translate}"
                )
            }
            checkNextQuestionAndSend(trainer, chatId)
        }
        if (receivedData == RESET_CLICKED) {
            dictionary.resetProgress()
            sendMessage(
                chatId,
                "Прогресс сброшен"
            )
            sendMenu(chatId)
        }
    }

    private fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, chatId: Long) {
        val question = trainer.getNextQuestion()
        if (question == null) {
            sendMessage(
                chatId,
                "Все слова в базе выучены!"
            )
            sendMenu(chatId)
        } else sendQuestion(chatId, question)
    }
}