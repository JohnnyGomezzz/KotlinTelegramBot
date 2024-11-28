package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId: Int? = 0
    val updateIdRegex: Regex = "\"update_id\":(\\d+),".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        println(updates)

        val updateIdMatchResult: MatchResult? = updateIdRegex.find(updates)
        val updateIdGroups = updateIdMatchResult?.groups
        updateId = updateIdGroups?.get(1)?.value?.toInt()?.plus(1)
        if (updateId == null) continue

        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value

        println(updateId)
        println(text)
    }
}

fun getUpdates(botToken: String, updateId: Int?): String {
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}