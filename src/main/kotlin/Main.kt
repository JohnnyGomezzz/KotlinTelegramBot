package org.example

fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index, word -> "${index + 1} - ${word.translate}" }
        .joinToString(
            "\n  ",
            "\n${this.correctAnswer.original}:\n  ",
            "\n-------------------\n0 - выход"
        )
    return variants
}

fun main() {

    val trainer = try {
        LearnWordsTrainer(3, 4)
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
                        } else if (userAnswerInput in (1..trainer.countOfQuestionWords)
                                .filter { it != question.variants.indexOf(question.correctAnswer).plus(1) }
                        ) {
                            println(
                                "Неправильно! ${question.correctAnswer.original} - это ${question.correctAnswer.translate}"
                            )
                        } else {
                            println("Введите номер ответа или 0 для выхода")
                            println(question.variants.indexOf(question.correctAnswer))
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