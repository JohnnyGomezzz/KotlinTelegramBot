package org.example

import java.io.File
import java.sql.DriverManager

fun main() {
    val wordsFile = File("words.txt")
    updateDictionary(wordsFile)
}

fun updateDictionary(wordsFile: File) {
    DriverManager.getConnection("jdbc:sqlite:data.db")
        .use { connection ->
            val statement = connection.createStatement()
            statement.executeUpdate(
                """
                    CREATE TABLE IF NOT EXISTS 'words' (
                    'id' integer PRIMARY KEY AUTOINCREMENT UNIQUE,
                    'text' varchar UNIQUE,
                    'translate' varchar,
                    'correct_answers_count' integer 
                );
            """.trimIndent()
            )
            wordsFile.readLines().forEach {
                val line = it.split("|")
                statement.executeUpdate(
                    String.format(
                        "insert into words values(null, \'%s\', \'%s\', %d)",
                        line[0],
                        line[1],
                        line.getOrNull(2)?.toIntOrNull() ?: 0,
                    )

                )
            }
        }
}