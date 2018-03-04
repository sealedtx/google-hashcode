import javafx.geometry.Point2D
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


val file = "b_should_be_easy.in"
val file_out = "out_$file"


fun main(args: Array<String>) {
    val scanner = Scanner(File(file))

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

    val stepIncrease = 150

    cars.forEach { car ->
        var currentStep = 0
        do {
            if (rides.isEmpty())
                return@forEach
//            if (step > rides[rides.size - 1].earliest)
//                currentStep = rides.size
            val someRides = getSomeRides(rides, currentStep, currentStep + stepIncrease)
            val ridesInTime = someRides.filter { car.loc.distance(it.start) + car.loc.distance(it.end) < it.latest - car.step }
            if (ridesInTime.isEmpty()) {
                if (currentStep > rides[rides.size - 1].earliest) {
                    car.step = T
                }
                currentStep += stepIncrease
                continue
            }
            val ridesLongToShort = ridesInTime
                    .sortedWith(Comparator { ride1, ride2 ->
                if (ride1.start.distance(ride1.end) < ride2.start.distance(ride2.end))
                    1
                else
                    -1
            })
            var ride = ridesLongToShort[0]
            val bonusRides = ridesInTime.filter { car.loc.distance(it.start) < it.earliest - car.step }
            if (bonusRides.isNotEmpty()) {
                ride = bonusRides.first()
            }

            car.rides.add(ride)
            val start = if (ride.earliest > car.step + car.loc.distance(ride.start)) ride.earliest else car.step + car.loc.distance(ride.start).toInt()
            car.step = (start + ride.start.distance(ride.end)).toInt()
            car.loc = ride.end
            rides.remove(ride)
        } while (car.step < T)
    }

    File(file_out).printWriter().use { out ->
        cars.forEach { car ->
            out.print("${car.rides.size} ")
            car.rides.forEach { ride ->
                out.print("${ride.index} ")
            }
            out.println()
        }
    }
}

fun getSomeRides(rides: ArrayList<Ride>, startStep: Int, endStep: Int): ArrayList<Ride> {
    return ArrayList(rides.filter { ride -> ride.earliest in startStep..endStep })
}

fun Point2D(x: Int, y: Int) = Point2D(x.toDouble(), y.toDouble())

class Car(val index: Int, var loc: Point2D, var step: Int, val rides: ArrayList<Ride> = arrayListOf()) {
    override fun toString(): String {
        return "Car is now on [${loc.x}, ${loc.y}], step $step"
    }
}

class Ride(val index: Int, val start: Point2D, val end: Point2D, val earliest: Int, val latest: Int) {
    override fun toString(): String {
        return "ride $index from [${start.x}, ${start.y}]  to [${end.x}, ${end.y}], earliest start $earliest, latest finish $latest"
    }
}