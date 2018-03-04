import java.io.File
import java.util.*


fun main(args: Array<String>) {
    val input = "d_metropolis.in"
    val output = "out_$input"

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

    val stepIncrease = 35

    var currentStep = 0

    rides.sortWith(kotlin.Comparator { ride1, ride2 ->
        when {
            ride1.earliest > ride2.earliest -> 1
            ride1.earliest == ride2.earliest -> 0
            else -> -1
        }
    })

    val skippedRides = arrayListOf<Ride>()

    while (rides.isNotEmpty()) {
        val ride = rides.first()
        // палим чи взагалі хтось встигає зробити
        val carsInTime = cars.filter { car ->
            val start = if (ride.earliest > car.step + car.loc.distance(ride.start)) ride.earliest else car.step + car.loc.distance(ride.start).toInt()
            start + ride.start.distance(ride.end) < ride.latest
        }
        if (carsInTime.isEmpty()) {
            skippedRides.add(ride)
            rides.remove(ride)
            continue
        }
        // якщо встигають, робим, якщо ні, скіпаєм райд
        if (carsInTime.isNotEmpty()) {
            // 1) найближчому в часі (якщо вже встигаєш на бонус, то нехай бере, того, що найбільший степ має
//            if (ride.earliest > 5000)
//                print("kek")

            val carsWithBonus = carsInTime.filter { car ->
                car.loc.distance(ride.start) < ride.earliest - car.step
            }
            // список машин сортуємо по:
            // 1) якщо є бонусні: найближчому в часі (якщо вже встигаєш на бонус, то нехай бере, того, що найбільший степ має
            // 2) якщо бонусів немає: найближчі у відстані берем
            val car: Car = if (carsWithBonus.isNotEmpty()) {
                carsWithBonus.sortedWith(Comparator { car1, car2 ->
                    when {
                        car1.step + car1.loc.distance(ride.start) > car2.step + car2.loc.distance(ride.start) -> 1
                        car1.step + car1.loc.distance(ride.start) == car2.step + car2.loc.distance(ride.start) -> 1
                        else -> -1
                    }
                }).first()
            } else {
                carsInTime.sortedWith(Comparator { car1, car2 ->
                    when {
                        car1.step + car1.loc.distance(ride.start) > car2.step + car2.loc.distance(ride.start) -> 1
                        car1.step + car1.loc.distance(ride.start) == car2.step + car2.loc.distance(ride.start) -> 0
                        else -> -1
                    }
                }).first()
            }
            car.rides.add(ride)
            val start = if (ride.earliest > car.step + car.loc.distance(ride.start)) ride.earliest else car.step + car.loc.distance(ride.start).toInt()
            car.step = (start + ride.start.distance(ride.end)).toInt()
            car.loc = ride.end
            rides.remove(ride)
        }

    }

    println(skippedRides.size)
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

