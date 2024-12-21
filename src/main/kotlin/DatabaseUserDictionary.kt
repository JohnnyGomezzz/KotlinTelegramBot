package org.example

import java.sql.DriverManager
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val DEFAULT_LEARNING_THRESHOLD = 3

class DatabaseUserDictionary(
    private val chatId: Long,
    private val learningThreshold: Int = DEFAULT_LEARNING_THRESHOLD,
) : IUserDictionary {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss")

    fun loadDictionary(userName: String) {
        val currentDate: String = LocalDateTime.now().format(formatter)
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
                    "insert or ignore into users(username, created_at, chat_id)\n" +
                                "VALUES('$userName', '$currentDate', $chatId);"
                )
                for (i in 1..getSize()) {
                    statement.executeUpdate(
                        "insert into user_answers (user_id, word_id, updated_at)\n" +
                                "VALUES(" +
                                "(SELECT id FROM users WHERE chat_id = $chatId), " +
                                i + ", '$currentDate');"
                    )
                }
            }
    }

    override fun getNumOfLearnedWords(): Int {
        var counter = 0
        DriverManager.getConnection("jdbc:sqlite:data.db")
            .use { connection ->
                val statement = connection.createStatement()
                val rs: ResultSet = statement.executeQuery(
                    """
                    |SELECT count(correct_answer_count)
                    |from user_answers
                    |where correct_answer_count >= 3;
                """.trimMargin()
                )
                while (rs.next()) {
                    counter = rs.getInt("count(correct_answer_count)")
                }
            }
        return counter
    }

    override fun getSize(): Int {
        var counter = 0
        DriverManager.getConnection("jdbc:sqlite:data.db")
            .use { connection ->
                val statement = connection.createStatement()
                val rs: ResultSet = statement.executeQuery(
                    """
                    |SELECT count(*)
                    |from words;
                """.trimMargin()
                )
                while (rs.next()) {
                    counter = rs.getInt("count(*)")
                }
            }
        return counter
    }

    override fun getLearnedWords(): List<Word> {
        val learnedWords = mutableListOf<Word>()
        DriverManager.getConnection("jdbc:sqlite:data.db")
            .use { connection ->
                val statement = connection.createStatement()
                val rs: ResultSet = statement.executeQuery(
                    """
                    SELECT text, translate from words
                    JOIN user_answers
                    on words.id = user_answers.word_id
                    where correct_answer_count >= $learningThreshold and user_id = 
                    (SELECT id from users WHERE chat_id = $chatId);
                """.trimMargin()
                )
                while (rs.next()) {
                    learnedWords.add(Word(rs.getString("text"), rs.getString("translate")))
                }
            }
        return learnedWords
    }

    override fun getUnlearnedWords(): List<Word> {
        val unlearnedWords = mutableListOf<Word>()
        DriverManager.getConnection("jdbc:sqlite:data.db")
            .use { connection ->
                val statement = connection.createStatement()
                val rs: ResultSet = statement.executeQuery(
                    """
                    SELECT text, translate from words
                    JOIN user_answers
                    on words.id = user_answers.word_id
                    where correct_answer_count < $learningThreshold and user_id = 
                    (SELECT id from users WHERE chat_id = $chatId);
                """.trimMargin()
                )
                while (rs.next()) {
                    unlearnedWords.add(Word(rs.getString("text"), rs.getString("translate")))
                }
            }
        return unlearnedWords
    }

    override fun setCorrectAnswersCount(word: String, correctAnswersCount: Int) {
        val currentDate: String = LocalDateTime.now().format(formatter)
        DriverManager.getConnection("jdbc:sqlite:data.db")
            .use { connection ->
                val statement = connection.createStatement()
                statement.executeUpdate(
                    """
                    UPDATE user_answers
                        SET correct_answer_count = $correctAnswersCount, updated_at = '$currentDate'
                        WHERE word_id = (SELECT id FROM words
                        WHERE text = '$word' AND user_id = (
                        SELECT id FROM users
                        WHERE chat_id = $chatId));
            """.trimIndent()
                )
            }

    }

    override fun resetProgress() {
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

}