package hash2020

import java.io.File
import java.util.*

val input = "c_incunabula.txt"
val output = "src/hash2020/out_$input"

fun main(args: Array<String>) {
    println()
    val scanner = Scanner(File(input))

    val B = scanner.nextInt()
    val L = scanner.nextInt()

    var DAYS_LEFT = scanner.nextInt()

    scanner.nextLine()
    val booksScores = scanner.nextLine().split(" ").mapIndexed { index, score -> index to score.toInt() }.toMap()

    var libraries = (0 until L).map { i ->
        val numberOfBooks = scanner.nextInt()
        val daysForSignUp = scanner.nextInt()
        val booksPerDay = scanner.nextInt()

        scanner.nextLine()
        var books = scanner.nextLine().split(" ").map { it.toInt() }.sortedByDescending { booksScores[it]!! }.toMutableList() // !!! ----
        val maxNumberOfBooksToScan = Math.min((DAYS_LEFT - daysForSignUp) * booksPerDay.toLong(), numberOfBooks.toLong()).toInt()

        books = books.take(maxNumberOfBooksToScan).toMutableList()

        val lib = Library(i, numberOfBooks, daysForSignUp, booksPerDay, books)
        lib.currentMaxScore = books.map { booksScores[it]!! }.sum()
//        lib.maxScorePerDay = lib.books.take(lib.booksPerDay).map { booksScores[it]!! }.sum()
        i to lib
    }.toMap().filter { (id, lib) -> DAYS_LEFT - lib.daysPerSignUp >= 0 }.toMutableMap()

    val scannedBooks = mutableSetOf<Int>()

    var sortedLibraries = libraries.values.sortedByDescending { lib -> lib.currentMaxScore / (0.5 * lib.daysPerSignUp.toDouble()) }
//    var sortedLibraries = libraries.values.sortedByDescending { lib -> lib.currentMaxScore }

    val result = mutableMapOf<Int, List<Int>>() // library_id -> library books that were scanned
    while (DAYS_LEFT > 0 && sortedLibraries.isNotEmpty()) {
        var topLib = sortedLibraries.first()

//        val tempLibs = sortedLibraries.filter { it.daysPerSignUp / 2 < topLib.daysPerSignUp && it.currentMaxScore > topLib.currentMaxScore / 2 }.sortedByDescending {
//            it.currentMaxScore / it.daysPerSignUp
//        }

//        if (tempLibs.size > 1) {
//            println("found two small")
//            val first = tempLibs.get(0)
//            val second = tempLibs.get(1)
//
//            libraries.remove(first.id)
//            libraries.remove(second.id)
//
//
//            DAYS_LEFT -= first.daysPerSignUp
//
//            var numberOfBooksToScan = Math.min(DAYS_LEFT * first.booksPerDay, first.books.size)
//            var booksThanWereScanned = (0 until numberOfBooksToScan).map { first.books.removeAt(0) }
//
//            if (booksThanWereScanned.isEmpty())
//                continue
//
//            scannedBooks.addAll(booksThanWereScanned)
//            result[first.id] = booksThanWereScanned
//
//
//            DAYS_LEFT -= second.daysPerSignUp
//
//            numberOfBooksToScan = Math.min(DAYS_LEFT * second.booksPerDay, second.books.size)
//            booksThanWereScanned = (0 until numberOfBooksToScan).map { second.books.removeAt(0) }
//
//            if (booksThanWereScanned.isEmpty())
//                continue
//
//            scannedBooks.addAll(booksThanWereScanned)
//            result[second.id] = booksThanWereScanned
//        } else {
//        tempLib?.let {
//            if (topLib.daysPerSignUp / tempLib.daysPerSignUp.toDouble() > topLib.currentMaxScore / tempLib.currentMaxScore.toDouble()) {
//                topLib = tempLib
//                println("take another lib: top lib = ${topLib.currentMaxScore}, ${topLib.daysPerSignUp}. temp lib = ${tempLib.currentMaxScore}, ${tempLib.daysPerSignUp}")
//            }
//        }

            libraries.remove(topLib.id)

            DAYS_LEFT -= topLib.daysPerSignUp

            val numberOfBooksToScan = Math.min(DAYS_LEFT * topLib.booksPerDay, topLib.books.size)
            val booksThanWereScanned = (0 until numberOfBooksToScan).map { topLib.books.removeAt(0) }

            if (booksThanWereScanned.isEmpty())
                continue

            scannedBooks.addAll(booksThanWereScanned)
            result[topLib.id] = booksThanWereScanned
//        }

        if (DAYS_LEFT <= 0)
            break

        libraries = libraries.filter { (_, lib) -> DAYS_LEFT - lib.daysPerSignUp >= 0 }.onEach { (k, lib) ->
            val daysForScanning = DAYS_LEFT - lib.daysPerSignUp
            val maxNumberOfBooksToScan = Math.min(daysForScanning * lib.booksPerDay.toLong(), lib.books.size.toLong()).toInt()
            lib.books = lib.books.filter { !scannedBooks.contains(it) }.take(maxNumberOfBooksToScan).toMutableList()
            lib.currentMaxScore = lib.books.map { booksScores[it]!! }.sum()
//            lib.maxScorePerDay = lib.books.take(lib.booksPerDay).map { booksScores[it]!! }.sum()
        }.toMutableMap()

        sortedLibraries = libraries.values.sortedByDescending { lib -> lib.currentMaxScore / (2 * lib.daysPerSignUp.toDouble()) }
//        sortedLibraries = libraries.values.sortedByDescending { lib -> lib.currentMaxScore }

    }


//    photos.sortBy { it.tags.size }

    File(output).printWriter().use { out ->
        out.println(result.size)

        result.forEach { (key, value) ->
            out.println("$key ${value.size}")
            out.println(value.joinToString(separator = " "))
        }
    }

}

class Library(val id: Int, val size: Int, val daysPerSignUp: Int, val booksPerDay: Int, var books: MutableList<Int>) {
    var currentMaxScore: Int = 0
    var maxScorePerDay: Int = 0

}

class Book(val id: Int, val score: Int) {

    override fun toString(): String {
        return "Book #$id, score $score"
    }

}
