package org.example

import java.sql.DriverManager

fun main() {
    DriverManager.getConnection("jdbc:sqlite:data.db")
        .use { connection ->
            val statement = connection.createStatement()
            statement.executeUpdate(
                """
                    CREATE TABLE IF NOT EXISTS 'words' (
                    'id' integer PRIMARY KEY AUTOINCREMENT UNIQUE,
                    'text' varchar UNIQUE,
                    'translate' varchar,
                    'correct_answers_count' varchar 
                );
            """.trimIndent()
            )
            statement.executeUpdate("insert into words values(null, \'cat\', \'кошка\', 0)")
        }
}