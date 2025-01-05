package org.example

import javax.naming.InvalidNameException

fun Question.asConsoleString(): String {
    if (variants.isEmpty()) {
        throw NullPointerException("Список вариантов пуст!")
    }
    if (variants.contains(variants.find {
            it.original.contains("[^\\w\\s]+".toRegex()) ||
            it.translate.contains("[^а-яА-ЯёЁ\\s]+".toRegex())
        })) {
        throw InvalidNameException("Одно слово или несколько слов содержат недопустимые символы!")
    }
    if (variants.contains(variants.find {
            it.original.contains("[a-zA-Zа-яА-ЯёЁ]".toRegex()).not() ||
                    it.translate.contains("[a-zA-Zа-яА-ЯёЁ]".toRegex()).not()
        })) {
        throw InvalidNameException("Одно слово или несколько слов не содержат буквенных символов!")
    }
    if (variants.size > 10) {
        variants = variants.take(10)
    }

    val printVariants = variants
        .mapIndexed { index, word -> "${index + 1} - ${word.translate}" }
        .joinToString(
            "\n  ",
            "\n${correctAnswer.original}:\n  ",
            "\n-------------------\n0 - выход"
        )
    return printVariants
}

fun main() {

    val trainer = try {
        LearnWordsTrainer(
            chatId = 1332884769,
            countOfQuestionWords = 4,
        )
    } catch (e: Exception) {
        println("Невозможно загрузить словарь")
        return
    }

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
                while (true) {
                    val question = trainer.getNextQuestion()
                    if (question == null) {
                        break
                    } else {
                        println(
                            question.asConsoleString()
                        )

                        val userAnswerInput = readln().toIntOrNull()
                        if (userAnswerInput == 0) break

                        if (trainer.checkAnswer(userAnswerInput?.minus(1))) {
                            println("Правильно!")
                        } else {
                            println("Неправильно! ${question.correctAnswer.original} - это ${question.correctAnswer.translate}")
                        }
                    }
                }
                println("Все слова в словаре выучены!")
            }

            "2" -> {
                val statistics = trainer.getStatistics()
                println(
                    String.format(
                        "Выучено %d из %d слов | %d%%",
                        statistics.learnedWords,
                        statistics.totalCount,
                        statistics.percent
                    )
                )
            }

            "0" -> return

            else -> println("Введите число 1, 2 или 0")
        }
    }
}