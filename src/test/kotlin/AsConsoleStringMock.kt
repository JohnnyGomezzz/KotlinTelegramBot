import org.example.Question
import org.example.Word

class AsConsoleStringMock {

    val test4VariantsQuestion = Question(
        variants = mutableListOf(
            Word("cat", "кошка"),
            Word("deer", "олень"),
            Word("dog", "собака"),
            Word("plane", "самолёт"),
        ),
        correctAnswer = Word("dog", "собака")
    )

    val test4VariantsNewOrderQuestion = Question(
        variants = mutableListOf(
            Word("cat", "кошка"),
            Word("deer", "олень"),
            Word("plane", "самолёт"),
            Word("dog", "собака"),
        ),
        correctAnswer = Word("dog", "собака")
    )

    val testEmptyListQuestion = Question(listOf(), Word("", ""))

    val test10VariantsQuestion = Question(
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

    val testMoreThan10VariantsQuestion = Question(
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

    val test4VariantsSymbolsQuestion = Question(
        variants = mutableListOf(
            Word("cat", "кошка"),
            Word("deer", "о(лень"),
            Word("dog", "соба ка"),
            Word("plane", "самолёт"),
        ),
        correctAnswer = Word("dog", "собака")
    )

    val test4VariantsSpaceQuestion = Question(
        variants = mutableListOf(
            Word(" ", "кошка"),
            Word("deer", " "),
            Word(" ", "собака"),
            Word("plane", "самолёт"),
        ),
        correctAnswer = Word("dog", "собака")
    )

}