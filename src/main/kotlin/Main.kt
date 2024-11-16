package org.example

import java.io.File
import kotlin.math.roundToInt

private const val NUM_OF_ANSWER_VARIANTS = 4

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
            "1" -> {
                println("Вы выбрали \"Учить слова\"")
                getLearning(dictionary)
            }

            "2" -> {
                println("Вы выбрали \"Статистика\"")
                getStatistics(dictionary)
            }

            "0" -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun getLearning(dictionary: MutableList<Word>) {

    do {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < 3 }
        val questionWords = notLearnedList.shuffled().take(NUM_OF_ANSWER_VARIANTS).toMutableList()
        val correctAnswer = questionWords.shuffled()[0]
        if (questionWords.size < NUM_OF_ANSWER_VARIANTS) {
            questionWords += dictionary.take(NUM_OF_ANSWER_VARIANTS - questionWords.size)
        }

        println(
            String.format(
                """
                                |
                                |%s:
                                |1 - %s
                                |2 - %s
                                |3 - %s
                                |4 - %s
                            """.trimMargin(),
                correctAnswer.original,
                questionWords[0].translate,
                questionWords[1].translate,
                questionWords[2].translate,
                questionWords[3].translate,
            )
        )
        val userAnswerInput = readln()
    } while (notLearnedList.size == 1)
}

fun getStatistics(dictionary: MutableList<Word>) {
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