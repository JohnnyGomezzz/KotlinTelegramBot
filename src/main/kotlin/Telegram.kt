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
    }
}

fun getFromUpdates(dataRegex: Regex, updates: String): String? {
    val matchResult: MatchResult? = dataRegex.find(updates)
    val groups = matchResult?.groups
    return groups?.get(1)?.value
}
