package org.example

import java.io.File
import kotlin.math.roundToInt

fun main() {
    val wordsFile = File("words.txt")
    val dictionary: MutableList<Word> = loadDictionary(wordsFile)

    while (true) {

        println(
            """
                |
        |Меню:
        |1 - Учить слова
        |2 - Статистика
        |0 - Выход
    """.trimMargin()
        )

        val choice = readln()
        when (choice) {
            "1" -> println("Вы выбрали \"Учить слова\"")
            "2" -> {
                println("Вы выбрали \"Статистика\"")

                val learnedWords = dictionary.filter { it.correctAnswersCount >= 3 }
                val learnedCount = learnedWords.size
                val totalCount = dictionary.size
                val percent = learnedCount.toDouble() / totalCount.toDouble() * 100.0

                println(
                    String.format(
                        "Выучено %d из %d слов | %d%%",
                        learnedCount,
                        totalCount,
                        percent.roundToInt()
                    )
                )
            }

            "0" -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun loadDictionary(wordsFile: File): MutableList<Word> {

    val dictionary: MutableList<Word> = mutableListOf()

    val lines: List<String> = wordsFile.readLines()
    for (l in lines) {
        val line = l.split("|")
        val word = Word(
            original = line[0],
            translate = line[1],
            correctAnswersCount = line.getOrNull(2)?.toIntOrNull() ?: 0
        )
        dictionary.add(word)
    }
    return dictionary
}