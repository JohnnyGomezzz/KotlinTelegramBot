package org.example

fun main(args: Array<String>) {
    val service = TelegramBotService(args[0])
    var updateId = 0

    val updateIdRegex: Regex = "\"update_id\":(\\d+),".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":.\"id\":(\\d+),".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates: String = service.getUpdates(updateId)
        println(updates)

        val updateIdMatchResult: MatchResult? = updateIdRegex.find(updates)
        val updateIdGroups = updateIdMatchResult?.groups
        updateId = updateIdGroups?.get(1)?.value?.toIntOrNull()?.plus(1) ?: continue

        val chatIdMatchResult: MatchResult? = chatIdRegex.find(updates)
        val chatIdGroups = chatIdMatchResult?.groups
        val chatId = chatIdGroups?.get(1)?.value?.toIntOrNull() ?: continue

        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val receivedText = groups?.get(1)?.value

        if (receivedText.equals("hello", ignoreCase = true)) service.sendMessage(chatId, "Hello!")
    }
}