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

}