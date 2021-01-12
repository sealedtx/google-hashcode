package hash2018

import java.io.File
import java.util.*


val input = "d_metropolis.in"
val output = "out_$input"

fun main(args: Array<String>) {
    val scanner = Scanner(File(input))

    val R = scanner.nextInt()
    val C = scanner.nextInt()
    val F = scanner.nextInt()
    val N = scanner.nextInt()
    val B = scanner.nextInt()
    val T = scanner.nextInt()

    val rides = arrayListOf<Ride>()
    val cars = Array(F, { i -> Car(i, Point2D(0, 0), 0) })

    for (i in 0 until N) {
        val a = scanner.nextInt()
        val b = scanner.nextInt()
        val x = scanner.nextInt()
        val y = scanner.nextInt()
        val s = scanner.nextInt()
        val f = scanner.nextInt()
        rides.add(Ride(i, Point2D(a, b), Point2D(x, y), s, f))
    }
    val skippedRides = arrayListOf<Ride>()

    val stepIncrease = 77
    var currentStep = 0

    while (currentStep <= T) {
        if (rides.isEmpty())
            break
        val someRides = getSomeRides(rides, currentStep, currentStep + stepIncrease) // вибірка по кроку
        // для кожного райду шукаєм оптимальну машину
        someRides.forEach { ride ->
            // палим чи взагалі хтось встигає зробити
            val carsInTime = cars.filter { car ->
                val start = if (ride.earliest > car.step + car.loc.distance(ride.start)) ride.earliest else car.step + car.loc.distance(ride.start).toInt()
                start + ride.start.distance(ride.end) < ride.latest
            }
            // якщо встигають, робим, якщо ні, скіпаєм райд
            if (carsInTime.isNotEmpty()) {
                var skip = false
                // чекаєм чи є машини, що встигають з бонусом
                val carsWithBonus = carsInTime.filter { car ->
                    car.loc.distance(ride.start) < ride.earliest - car.step
                }
                // список машин сортуємо по:
                // 1) якщо є бонусні: найближчому в часі (якщо вже встигаєш на бонус, то нехай бере, того, що найбільший степ має
                // 2) якщо бонусів немає: найближчі у відстані берем
                val car: Car = if (carsWithBonus.isNotEmpty()) {
                    val check = carsWithBonus.sortedWith(Comparator { car1, car2 ->
                        when {
                            car1.step + car1.loc.distance(ride.start) > car2.step + car2.loc.distance(ride.start) -> 1
                            car1.step + car1.loc.distance(ride.start) == car2.step + car2.loc.distance(ride.start) -> 1
                            else -> -1
                        }
                    }).first()
                    if (ride.start.distance(ride.end) < check.loc.distance(ride.start) / 5) {
                        println("hash2018.Ride distance ${ride.start.distance(ride.end)}, car distance ${check.loc.distance(ride.start)}")
                        skippedRides.add(ride)
                        skip = true
                    }
                    check
                } else {
                    val check = carsInTime.sortedWith(Comparator { car1, car2 ->
                        when {
                            car1.step + car1.loc.distance(ride.start) > car2.step + car2.loc.distance(ride.start) -> 1
                            car1.step + car1.loc.distance(ride.start) == car2.step + car2.loc.distance(ride.start) -> 0
                            else -> -1
                        }
                    }).first()
                    if (ride.start.distance(ride.end) < check.loc.distance(ride.start) / 2) {
                        println("hash2018.Ride distance ${ride.start.distance(ride.end)}, car distance ${check.loc.distance(ride.start)}")
                        skippedRides.add(ride)
                        skip = true
                    }
                    check
                }
                if (!skip) {
                    car.rides.add(ride)
                    val start = if (ride.earliest > car.step + car.loc.distance(ride.start)) ride.earliest else car.step + car.loc.distance(ride.start).toInt()
                    car.step = (start + ride.start.distance(ride.end)).toInt()
                    car.loc = ride.end
                    rides.remove(ride)
                }
            } else {
                skippedRides.add(ride)
            }
        }
        currentStep += stepIncrease
    }

    println("Skipped rides: ${skippedRides.size}")
    File(output).printWriter().use { out ->
        cars.forEach { car ->
            out.print("${car.rides.size} ")
            car.rides.forEach { ride ->
                out.print("${ride.index} ")
            }
            out.println()
        }
    }

}
