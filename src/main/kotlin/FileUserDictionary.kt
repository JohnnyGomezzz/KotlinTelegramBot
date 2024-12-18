package org.example

import java.io.File

const val DEFAULT_FILE_NAME = "words.txt"
const val DEFAULT_LEARNING_THRESHOLD = 3

class FileUserDictionary(
    private val fileName: String = DEFAULT_FILE_NAME,
    private val learningThreshold: Int = DEFAULT_LEARNING_THRESHOLD,
) : IUserDictionary {

    private val dictionary = try {
        loadDictionary()
    } catch (e: Exception) {
        throw IllegalArgumentException("Некорректный файл")
    }

    fun loadDictionary(): MutableList<Word> {
        try {
            val dictionary: MutableList<Word> = mutableListOf()
            val wordsFile = File(fileName)
            if (!wordsFile.exists()) {
                File("words.txt").copyTo(wordsFile)
            }
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

    private fun saveDictionary() {
        val wordsFile = File(fileName)
        wordsFile.writeText("")
        dictionary.forEach { wordsFile.appendText("${it.original}|${it.translate}|${it.correctAnswersCount}\n") }
    }

    override fun resetProgress() {
        dictionary.forEach {
            it.correctAnswersCount = 0
            saveDictionary()
        }
    }

    override fun setCorrectAnswersCount(word: String, correctAnswersCount: Int) {
        dictionary.find { it.original == word }?.correctAnswersCount = correctAnswersCount
        saveDictionary()
    }

    override fun getNumOfLearnedWords(): Int {
        TODO("Not yet implemented")
    }

    override fun getSize(): Int {
        TODO("Not yet implemented")
    }

    override fun getLearnedWords(): List<Word> {
        TODO("Not yet implemented")
    }

    override fun getUnlearnedWords(): List<Word> {
        TODO("Not yet implemented")
    }

}