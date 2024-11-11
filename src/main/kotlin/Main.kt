package org.example

import java.io.File

fun main() {
    val wordsFile: File = File("words.txt")

    for (line in wordsFile.readLines()) {
        println(line)
    }
}