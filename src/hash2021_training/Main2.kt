package hash2021_training

import java.io.File
import java.util.*
import kotlin.collections.ArrayList

fun main(args: Array<String>) {
    val scanner = Scanner(File(input))

    val M = scanner.nextInt()
    val T2 = scanner.nextInt()
    val T3 = scanner.nextInt()
    val T4 = scanner.nextInt()
    val teams = mutableMapOf(2 to T2, 3 to T3, 4 to T4)

    val originalPizzas = (0 until M).map {
        scanner.nextInt()
        val ingredients = scanner.nextLine().split(" ").filterNot { it.isEmpty() }.toSet()
        Pizza(it, ingredients)
    }.toMutableList()

    // sort by number of ingredients desc, the first - the most ingredients
    originalPizzas.sortByDescending { it.size() }

    // copy
    val pizzas = originalPizzas.toMutableList()
    pizzas.sortByDescending { it.size() }

    val deliveredOrders = mutableListOf<Order>()
    val deliveredPizzas = mutableSetOf<Int>()

//    val start = System.currentTimeMillis()
    while (pizzas.isNotEmpty()) {
        val requiredSize = if (teams[2]!! > 0 && pizzas.size >= 2) {
            2
        } else if (teams[3]!! > 0 && pizzas.size > 3 && pizzas.size > 4) {
            3
        } else if (teams[4]!! > 0 && pizzas.size > 5) {
            4
        } else if (teams[3]!! > 0 && pizzas.size >= 3) {
            3
        } else if (teams[4]!! > 0 && pizzas.size >= 4) {
            4
        } else {
            break // no pizzas left
        }
        val preparedPizzas = ArrayList<Pizza>(4)
        val preparedOrder = Order(preparedPizzas)

        // first pizza
        var firstPizza = pizzas.removeFirst()
        // check if already delivered in another order
//        while (deliveredPizzas.contains(firstPizza.id)) {
//            firstPizza = originalPizzas.removeFirst()
//        }
        preparedPizzas.add(firstPizza)

        while (preparedPizzas.size < requiredSize) {
            originalPizzas.sortByDescending {
                val existingPizzas = preparedOrder.pizzas.flatMap { it.ingredients }
                val newSet = mutableSetOf<String>()
                newSet.addAll(existingPizzas.toSet())
                newSet.addAll(it.ingredients)

                val total = existingPizzas.size + it.size()
                (newSet.size * newSet.size)
            }
            val pizza = pizzas.removeFirst()
            preparedPizzas.add(pizza)
//            deliveredPizzas.add(pizza.id)
        }
        deliveredOrders.add(preparedOrder)
        println("created order score = ${preparedOrder.score(true)} pizzas left ${pizzas.size}")

        pizzas.sortByDescending { it.size() }
    }

    var score = 0.0
    println(deliveredOrders.size)
    deliveredOrders.forEach { order ->
        println("${order.pizzas.size} ${order.pizzas.map { it.id }.joinToString(separator = " ")}")
        score += order.score(recalculate = true)
    }
    println(score)

    File(output).printWriter().use { out ->
        out.println(deliveredOrders.size)
        deliveredOrders.forEach { order ->
            out.println("${order.pizzas.size} ${order.pizzas.map { it.id }.joinToString(separator = " ")}")
        }
    }

}

