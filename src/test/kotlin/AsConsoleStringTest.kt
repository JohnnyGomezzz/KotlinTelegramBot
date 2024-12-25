import org.example.Question
import org.example.Word
import org.example.asConsoleString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.naming.InvalidNameException
import kotlin.test.assertEquals

class AsConsoleStringTest {

    @Test
    fun test4Variants() {
        val question = Question(
            variants = mutableListOf(
                Word("cat", "кошка"),
                Word("deer", "олень"),
                Word("dog", "собака"),
                Word("plane", "самолёт"),
            ),
            correctAnswer = Word("dog", "собака")
        )
        val result = question.asConsoleString()
        assertEquals(
            """
        |
        |dog:
        |  1 - кошка
        |  2 - олень
        |  3 - собака
        |  4 - самолёт
        |-------------------
        |0 - выход
    """.trimMargin(),
            result
        )
    }

    @Test
    fun test4VariantsNewOrder() {
        val question = Question(
            variants = mutableListOf(
                Word("cat", "кошка"),
                Word("deer", "олень"),
                Word("plane", "самолёт"),
                Word("dog", "собака"),
            ),
            correctAnswer = Word("dog", "собака")
        )
        val result = question.asConsoleString()
        assertEquals(
            """
        |
        |dog:
        |  1 - кошка
        |  2 - олень
        |  3 - самолёт
        |  4 - собака
        |-------------------
        |0 - выход
    """.trimMargin(),
            result
        )
    }

    @Test
    fun testEmptyList() {
        val exception = assertThrows<NullPointerException> {
            val question = Question(listOf(), Word("", ""))
            question.asConsoleString()
        }
        assertEquals("Список вариантов пуст!", exception.message)
    }

    @Test
    fun test10Variants() {
        val question = Question(
            variants = mutableListOf(
                Word("cat", "кошка"),
                Word("deer", "олень"),
                Word("dog", "собака"),
                Word("plane", "самолёт"),
                Word("circle", "круг"),
                Word("square", "квадрат"),
                Word("triangle", "треугольник"),
                Word("lamp", "лампа"),
                Word("mouse", "мышь"),
                Word("pen", "ручка"),
            ),
            correctAnswer = Word("dog", "собака")
        )
        val result = question.asConsoleString()
        assertEquals(
            """
        |
        |dog:
        |  1 - кошка
        |  2 - олень
        |  3 - собака
        |  4 - самолёт
        |  5 - круг
        |  6 - квадрат
        |  7 - треугольник
        |  8 - лампа
        |  9 - мышь
        |  10 - ручка
        |-------------------
        |0 - выход
    """.trimMargin(),
            result
        )
    }

    @Test
    fun testMoreThan10Variants() {
        val question = Question(
            variants = mutableListOf(
                Word("cat", "кошка"),
                Word("deer", "олень"),
                Word("dog", "собака"),
                Word("plane", "самолёт"),
                Word("circle", "круг"),
                Word("square", "квадрат"),
                Word("triangle", "треугольник"),
                Word("lamp", "лампа"),
                Word("mouse", "мышь"),
                Word("pen", "ручка"),
                Word("circle", "круг"),
                Word("square", "квадрат"),
                Word("triangle", "треугольник"),
                Word("lamp", "лампа"),
                Word("mouse", "мышь"),
                Word("pen", "ручка"),
                Word("circle", "круг"),
                Word("square", "квадрат"),
                Word("triangle", "треугольник"),
                Word("lamp", "лампа"),
                Word("mouse", "мышь"),
                Word("pen", "ручка"),
                Word("circle", "круг"),
                Word("square", "квадрат"),
                Word("triangle", "треугольник"),
                Word("lamp", "лампа"),
                Word("mouse", "мышь"),
                Word("pen", "ручка"),
            ),
            correctAnswer = Word("dog", "собака")
        )
        val result = question.asConsoleString()
        assertEquals(
            """
        |
        |dog:
        |  1 - кошка
        |  2 - олень
        |  3 - собака
        |  4 - самолёт
        |  5 - круг
        |  6 - квадрат
        |  7 - треугольник
        |  8 - лампа
        |  9 - мышь
        |  10 - ручка
        |-------------------
        |0 - выход
    """.trimMargin(),
            result
        )
    }

    @Test
    fun test4VariantsSymbols() {
        val exception = assertThrows<InvalidNameException> {
            val question = Question(
                variants = mutableListOf(
                    Word("cat", "кошка"),
                    Word("deer", "о(лень"),
                    Word("dog", "соба ка"),
                    Word("plane", "самолёт"),
                ),
                correctAnswer = Word("dog", "собака")
            )
            question.asConsoleString()
        }
        assertEquals("Одно слово или несколько слов содержат недопустимые символы!", exception.message)
    }

    @Test
    fun test4VariantsSpace() {
        val exception = assertThrows<InvalidNameException> {
            val question = Question(
                variants = mutableListOf(
                    Word(" ", "кошка"),
                    Word("deer", " "),
                    Word(" ", "собака"),
                    Word("plane", "самолёт"),
                ),
                correctAnswer = Word("dog", "собака")
            )
            question.asConsoleString()
        }
        assertEquals("Одно слово или несколько слов не содержат буквенных символов!", exception.message)
    }
}