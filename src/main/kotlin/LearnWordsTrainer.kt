package org.example

import java.io.File
import kotlin.math.roundToInt

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

data class Statistics(
    val learnedWords: Int,
    val totalCount: Int,
    val percent: Int,
)

data class Question(
    var variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    private val learnedAnswersCount: Int = 3,
    private val countOfQuestionWords: Int = 4,
) {
    var question: Question? = null
    private val dictionary: MutableList<Word> = loadDictionary()

    fun getStatistics(): Statistics {

        val learnedWords = dictionary.filter { it.correctAnswersCount >= learnedAnswersCount }.size
        val totalCount = dictionary.size
        val percent = (learnedWords.toDouble() / totalCount.toDouble() * 100.0).roundToInt()

        return Statistics(learnedWords, totalCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < learnedAnswersCount }
        if (notLearnedList.isEmpty()) return null

        val questionWords = if (notLearnedList.size < countOfQuestionWords) {
            val learnedList = dictionary.filter { it.correctAnswersCount >= learnedAnswersCount }
            notLearnedList.shuffled().take(countOfQuestionWords) + learnedList.shuffled()
                .take(countOfQuestionWords - notLearnedList.size)
        } else {
            notLearnedList.shuffled().take(countOfQuestionWords)
        }.shuffled()

        val correctAnswer = questionWords.random()

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
            } else false
        } ?: false
    }

    private fun loadDictionary(): MutableList<Word> {
        try {
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
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("Некорректный файл")
        }
    }

    private fun saveDictionary(dictionary: MutableList<Word>) {
        val wordsFile = File("words.txt")
        wordsFile.writeText("")
        dictionary.forEach { wordsFile.appendText("${it.original}|${it.translate}|${it.correctAnswersCount}\n") }
    }
}