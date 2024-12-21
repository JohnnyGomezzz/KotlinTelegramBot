package org.example

import java.io.File
import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {

    val formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss")
    val currentDate: String = LocalDateTime.now().format(formatter)

    resetProgress()
    loadDictionary("sgfnhfg", currentDate, 73598732)
}

fun loadDictionary(userName: String, currentDate: String, chatId: Long) {
    DriverManager.getConnection("jdbc:sqlite:data.db")
        .use { connection ->
            val statement = connection.createStatement()
            statement.executeUpdate(
                """
                    CREATE TABLE IF NOT EXISTS 'users' (
	                'id'	integer DEFAULT 0,
	                'username'	varchar,
	                'created_at'	timestamp,
	                'chat_id'	bigint,
	                PRIMARY KEY('id' AUTOINCREMENT));

                    CREATE TABLE IF NOT EXISTS 'user_answers' (
	                'user_id'	integer DEFAULT 0,
	                'word_id'	integer,
	                'correct_answer_count'	integer DEFAULT 0,
	                'updated_at'	timestamp,
	                FOREIGN KEY('user_id') REFERENCES 'users'('id'),
	                FOREIGN KEY('word_id') REFERENCES 'words'('id')
);
            """.trimIndent()
            )
            statement.executeUpdate(
                "insert into users (username, created_at, chat_id)\n" +
                        "VALUES('$userName', '$currentDate', $chatId);"
            )
        }
}

fun resetProgress() {
    DriverManager.getConnection("jdbc:sqlite:data.db")
        .use { connection ->
            val statement = connection.createStatement()
            statement.executeUpdate(
                """
                    DELETE FROM user_answers
                    WHERE user_id = 2;
            """.trimIndent()
            )
        }
}

//fun getLearnedWords(): List<Word> {
//    DriverManager.getConnection("jdbc:sqlite:data.db")
//        .use { connection ->
//            val statement = connection.createStatement()
//            val rs: ResultSet = statement.executeQuery(
//                """
//                    |SELECT count(*)
//                    |from words;
//                """.trimMargin()
//            )
//            while (rs.next()) {
//                counter = rs.getString("count(*)")
//            }
//        }
//}

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
                        "insert into words values(null, \'%s\', \'%s\')",
                        line[0],
                        line[1],
                    )

                )
            }
        }
}