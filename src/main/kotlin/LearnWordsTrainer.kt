package org.example

import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
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
    chatId: Long,
    private val countOfQuestionWords: Int = 4,
) {
    var question: Question? = null
    private val dictionary = DatabaseUserDictionary(chatId)

    fun getStatistics(): Statistics {

        val learnedWords = dictionary.getLearnedWords().size
        val totalCount = dictionary.getSize()
        val percent = (learnedWords.toDouble() / totalCount.toDouble() * 100.0).roundToInt()

        return Statistics(learnedWords, totalCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.getUnlearnedWords()
        if (notLearnedList.isEmpty()) return null

        val questionWords = if (notLearnedList.size < countOfQuestionWords) {
            val learnedList = dictionary.getLearnedWords()
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
                dictionary.setCorrectAnswersCount(it.correctAnswer.original, it.correctAnswer.correctAnswersCount.plus(1))
                true
            } else false
        } ?: false
    }
}