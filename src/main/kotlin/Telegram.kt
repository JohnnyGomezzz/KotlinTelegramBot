package org.example

fun main(args: Array<String>) {
    val service = TelegramBotService(args[0])
    val trainer = LearnWordsTrainer()
    var updateId = 0

    val updateIdRegex: Regex = "\"update_id\":(\\d+),".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+),".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates: String = service.getUpdates(updateId)
        println(updates)

        updateId = getFromUpdates(updateIdRegex, updates)?.toIntOrNull()?.plus(1) ?: continue
        val chatId = getFromUpdates(chatIdRegex, updates)?.toLongOrNull() ?: continue
        val receivedText = getFromUpdates(messageTextRegex, updates)
        val receivedData = getFromUpdates(dataRegex, updates)

        if (receivedText == "/start".lowercase()) service.sendMenu(chatId)
        if (receivedData == STATISTICS_CLICKED) {
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
        if (receivedData == LEARN_WORDS_CLICKED) checkNextQuestionAndSend(trainer, service, chatId)
    }
}

fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, service: TelegramBotService, chatId: Long) {
    val question = trainer.getNextQuestion()
    if (question == null) {
        service.sendMessage(
            chatId,
            "Все слова в базе выучены!"
        )
    } else service.sendQuestion(chatId, question)
}

fun getFromUpdates(dataRegex: Regex, updates: String): String? {
    val matchResult: MatchResult? = dataRegex.find(updates)
    val groups = matchResult?.groups
    return groups?.get(1)?.value
}
