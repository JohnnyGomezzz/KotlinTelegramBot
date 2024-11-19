package org.example

import java.io.File
import kotlin.math.roundToInt

data class Statistics(
    val learnedWords: Int,
    val totalCount: Int,
    val percent: Int,
)

data class Question(
    var variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {

    private var question: Question? = null
    private val dictionary: MutableList<Word> = loadDictionary()

    fun getStatistics(): Statistics {

        val learnedWords = dictionary.filter { it.correctAnswersCount >= 3 }.size
        val totalCount = dictionary.size
        val percent = (learnedWords.toDouble() / totalCount.toDouble() * 100.0).roundToInt()

        return Statistics(learnedWords, totalCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < 3 }
        if (notLearnedList.isEmpty()) return null

        val questionWords = notLearnedList.take(NUM_OF_ANSWER_VARIANTS).shuffled().toMutableList()
        val correctAnswer = questionWords.random()

        if (questionWords.size < NUM_OF_ANSWER_VARIANTS) {
            questionWords +=
                dictionary.shuffled().take(NUM_OF_ANSWER_VARIANTS - questionWords.size)
        }

        question = Question(
            variants = questionWords,
            correctAnswer = correctAnswer,
        )
        return question
    }

    fun checkAnswer(userAnswerId: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerId) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(dictionary)
                true
            }  else false
        } ?: false
    }

    private fun loadDictionary(): MutableList<Word> {
        val dictionary: MutableList<Word> = mutableListOf()
        val wordsFile = File("words.txt")

        wordsFile.readLines().forEach {
            val line = it.split("|")
            dictionary.add(
                Word(
                    original = line[0],
                    translate = line[1],
                    correctAnswersCount = line.getOrNull(2)?.toIntOrNull() ?: 0
                )
            )
        }
        return dictionary
    }

    private fun saveDictionary(dictionary: MutableList<Word>) {
        val wordsFile = File("words.txt")
        wordsFile.writeText("")
        dictionary.forEach { wordsFile.appendText("${it.original}|${it.translate}|${it.correctAnswersCount}\n") }
    }
}