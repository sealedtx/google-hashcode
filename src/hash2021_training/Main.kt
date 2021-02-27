package hash2021_training

import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.math.pow

val file = "a_example.in"
val root = "src/hash2021_training"
val input = "$root/$file"
val output = "$root/out_$file"

fun main(args: Array<String>) {
    val scanner = Scanner(File(input))

    val M = scanner.nextInt()
    val T2 = scanner.nextInt()
    val T3 = scanner.nextInt()
    val T4 = scanner.nextInt()
    val teams = mutableMapOf(2 to T2, 3 to T3, 4 to T4)

    val pizzas = (0 until M).map {
        scanner.nextInt()
        val ingredients = scanner.nextLine().split(" ").filterNot { it.isEmpty() }.toSet()
        it to Pizza(it, ingredients)
    }.toMap().toMutableMap()

    val deliveredPizzas = mutableSetOf<Int>()
    val deliveredOrders = mutableListOf<Order>()

//    val start = System.currentTimeMillis()
    while (pizzas.isNotEmpty()) {
        val searchCombinationSize = if (teams[2]!! > 0 && pizzas.size >= 2) {
            2
        } else if (teams[4]!! > 0 && pizzas.size > 5) {
            4
        } else if (teams[3]!! > 0 && pizzas.size > 3 && pizzas.size > 4) {
            3
        } else if (teams[4]!! > 0 && pizzas.size >= 4) {
            4
        } else if (teams[3]!! > 0 && pizzas.size >= 3) {
            3
        } else {
            break // no pizzas left
        }
        val maxCombinations = 20_000
        val maxOrdersPerShuffle = 10
        println("create combinations for $searchCombinationSize team size, max $maxCombinations")
//        val combinations = createOrders(pizzas.values, searchCombinationSize, maxCombinations)
        val combinations = pizzas.values.combinations(searchCombinationSize, maxCombinations).toMutableList()
        combinations.sortByDescending { it.score() }
//        println("Time spent ${System.currentTimeMillis() - start}")

        var ordersPerShuffle = 0
        run loop@{
            combinations.forEach { maxOrder ->
                val isReal = maxOrder.pizzas.none { deliveredPizzas.contains(it.id) }
                if (isReal && teams[searchCombinationSize]!! > 0) {
                    deliveredOrders.add(maxOrder)
                    teams[searchCombinationSize] = teams[searchCombinationSize]!! - 1
                    maxOrder.pizzas.forEach { pizza -> deliveredPizzas.add(pizzas.remove(pizza.id)!!.id) }

                    println("created order score = ${maxOrder.score()} pizzas left ${pizzas.size}")
                    if (++ordersPerShuffle >= maxOrdersPerShuffle) {
                        return@loop
                    }
                } else {
//                    println("some of pizzas already delivered ${maxOrder.pizzas.map { it.id }}")
                }
            }
        }
    }

    var score = 0.0
    println(deliveredOrders.size)
    deliveredOrders.forEach { order ->
        println("${order.pizzas.size} ${order.pizzas.map { it.id }.joinToString(separator = " ")}")
        score += order.score()
    }
    println(score)

    File(output).printWriter().use { out ->
        out.println(deliveredOrders.size)
        deliveredOrders.forEach { order ->
            out.println("${order.pizzas.size} ${order.pizzas.map { it.id }.joinToString(separator = " ")}")
        }
    }

}

class Pizza(val id: Int, val ingredients: Set<String>) {
    fun size(): Int {
        return ingredients.size
    }
}

class Order(val pizzas: List<Pizza>) {
    val ingredients: Set<String> = pizzas.flatMap { it.ingredients }.toSet()

    fun score(recalculate: Boolean = false): Double {
        if (recalculate)
            return pizzas.flatMap { it.ingredients }.toSet().size.toDouble().pow(2)
        return ingredients.size.toDouble().pow(2)
    }

}


fun Iterable<Pizza>.combinations(length: Int, limit: Int): Sequence<Order> =
        sequence {
            val pool = this@combinations.toList()
            val n = pool.size
            if (length > n) return@sequence
            val indices = IntArray(length) { it }
            var count = 0
            while (true) {
                yield(Order(indices.map { pool[it] }))
                if (++count >= limit) {
                    return@sequence
                }
                var i = length
                do {
                    i--
                    if (i == -1) return@sequence
                } while (indices[i] == i + n - length)
                indices[i]++
                for (j in i + 1 until length) indices[j] = indices[j - 1] + 1
            }
        }
