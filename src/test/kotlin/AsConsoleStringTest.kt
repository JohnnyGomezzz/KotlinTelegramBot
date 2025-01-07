import org.example.asConsoleString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.naming.InvalidNameException
import kotlin.test.assertEquals

class AsConsoleStringTest {

    private val mock = AsConsoleStringMock()

    @Test
    fun test4Variants() {
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
            mock.test4VariantsQuestion.asConsoleString()
        )
    }

    @Test
    fun test4VariantsNewOrder() {
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
            mock.test4VariantsNewOrderQuestion.asConsoleString()
        )
    }

    @Test
    fun testEmptyList() {
        val exception = assertThrows<NullPointerException> {
            mock.testEmptyListQuestion.asConsoleString()
        }
        assertEquals("Список вариантов пуст!", exception.message)
    }

    @Test
    fun test10Variants() {
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
            mock.test10VariantsQuestion.asConsoleString()
        )
    }

    @Test
    fun testMoreThan10Variants() {
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
            mock.testMoreThan10VariantsQuestion.asConsoleString()
        )
    }

    @Test
    fun test4VariantsSymbols() {
        val exception = assertThrows<InvalidNameException> {
            mock.test4VariantsSymbolsQuestion.asConsoleString()
        }
        assertEquals("Одно слово или несколько слов содержат недопустимые символы!", exception.message)
    }

    @Test
    fun test4VariantsSpace() {
        val exception = assertThrows<InvalidNameException> {
            mock.test4VariantsSpaceQuestion.asConsoleString()
        }
        assertEquals("Одно слово или несколько слов не содержат буквенных символов!", exception.message)
    }
}