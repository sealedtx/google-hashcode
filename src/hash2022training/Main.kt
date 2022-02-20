package hash2022training

import java.io.File
import java.lang.Double.max
import java.util.*

val file = "b_basic.in.txt"
val root = "src/hash2022training"
val input = "$root/$file"
val output = "$root/out_$file"

fun main(args: Array<String>) {
    val scanner = Scanner(File(input))

    val С = scanner.nextInt()

    val ingredientsMap = mutableMapOf<String, Ingredient>()
    val allClients = (0 until С).map { index ->
        scanner.nextInt()
        val likedIngredients = scanner.nextLine().split(" ").filterNot { it.isEmpty() }.onEach {
            val ingredient = ingredientsMap.computeIfAbsent(it) { Ingredient(it, 0.0, 0.0) }
            ingredient.numLikes += 1
        }.toSet()

        scanner.nextInt()
        val dislikedIngredients = scanner.nextLine().split(" ").filterNot { it.isEmpty() }.onEach {
            val ingredient = ingredientsMap.computeIfAbsent(it) { Ingredient(it, 0.0, 0.0) }
            ingredient.numDislikes += 1
        }.toSet()

        Client(index, likedIngredients, dislikedIngredients)
    }.toMutableList()

    // initially remove clients which does not dislike any ingredient
    var clients = allClients.filter { it.dislike.isNotEmpty() }.toMutableList()

    while (clients.size > 0) {
        // split clients into two groups:
        // 1) bad clients (which dislike some ingredient which is liked by more than 1 other client)
        // 2) good clients
        val splitted = clients.groupBy { client ->
            var badClient = false
            val numberOfClientsWhoLikesWhatCurrentDislike = client.dislike.map { igr -> ingredientsMap[igr]!!.numLikes }.sum()
            if (numberOfClientsWhoLikesWhatCurrentDislike > 1) {
                badClient = true
                client.dislike.forEach { igr ->
                    ingredientsMap[igr]!!.numDislikes -= 1
                }
                client.like.forEach { igr ->
                    ingredientsMap[igr]!!.numLikes -= 1
                }
            }

            badClient
        }
        val goodClients = splitted[false]!!
        val badClients = splitted[true]

        if (badClients == null) {
            break
        }

        clients = goodClients.toMutableList()
    }

    // remove ingredients which are disliked by left clients
    clients.forEach { client ->
        client.dislike.forEach { ingredientsMap.remove(it) }
    }


    print(ingredientsMap.size.toString() + " ")
    print(ingredientsMap.keys.joinToString(separator = " "))

    File(output).printWriter().use { out ->
        out.print(ingredientsMap.size.toString() + " ")
        out.print(ingredientsMap.keys.joinToString(separator = " "))
    }

}

data class Client(val id: Int, val like: Set<String>, val dislike: Set<String>) {

    fun calculateImportance(ingredientsMap: MutableMap<String, Ingredient>) {
        val likeScore = like.map { ingredientsMap[it]!!.numLikes }.sum()
        val dislikeScore = dislike.map { ingredientsMap[it]!!.numLikes }.sum()
        println("client $id like score: $likeScore dislike score: $dislikeScore")
    }
}

data class Ingredient(val name: String, var numLikes: Double, var numDislikes: Double) {

    // higher score means more clients likes this ingredient
    fun getLikesScore(): Double {
        return numLikes / max(numDislikes, 1.0)
    }
}