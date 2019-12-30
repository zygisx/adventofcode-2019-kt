package ten

import java.lang.IllegalArgumentException


data class Point(val x: Double, val y: Double) {

    fun angle(point: Point): Double {
        val xdelta = this.x - point.x
        val ydelta = this.y - point.y
        return Math.atan2(xdelta, ydelta)
    }

    fun distance(point: Point): Double {
        return Math.abs(this.x - point.x) + Math.abs(this.y - point.y)
    }
}

data class Meteor(val point: Point, val angle: Double, val distance: Double)

fun pointsSet(input: String): MutableSet<Point> {
    val pointsSet = mutableSetOf<Point>()
    val lines = input.split("\n")

    lines.forEachIndexed { y, line ->
        line.toCharArray().forEachIndexed { x, point ->
            when (point) {
                '#' -> pointsSet.add(Point(x.toDouble(), y.toDouble()))
                '.' -> {}
                else -> throw IllegalArgumentException("Unrecognized point: $point")
            }
        }
    }

    return pointsSet
}


fun main() {

    val p1 = Point(4.0, 4.0)
    val p2 = Point(4.0, 0.0)
    val p3 = Point(8.0, 4.0)
    val p4 = Point(4.0, 8.0)
    val p5 = Point(0.0, 4.0)

    println(p1.angle(p2))
    println(p1.angle(p3))
    println(p1.angle(p4))
    println(p1.angle(p5))
    println(p1.angle(p1))

    println("--------")

    val points = pointsSet(INPUT)

    val pointsWithUsedAngles = points.map { point ->
        val anglesUsed = points.filter { it != point }.map { point.angle(it) }.distinct()
        Pair(point, anglesUsed.size)
    }

    val max = pointsWithUsedAngles.maxBy { it.second }

    println("====FISRT====")
    println(max)

    println("====SECOND====")
    val station = max!!.first
    points.remove(station)

    val meteors = points
            .map { Meteor(it, station.angle(it), station.distance(it)) }
            .groupBy { it.angle }
            .mapValues { it.value.sortedBy { m -> m.distance }.toMutableList() }
            .toMutableMap()

    val DELTA = 0.0000000001
    var pointer = 0 + 0.0000000001

    for (i in 0 until 200) {
        val nextAngle = meteors.keys.filter { it < pointer }.max()
        if (nextAngle != null) {
            val killed = meteors[nextAngle]!!.removeAt(0)
            println("$i X:${killed.point.x},Y:${killed.point.y} D:${killed.distance} A:${killed.angle}")
            if (meteors[nextAngle]!!.isEmpty()) {
                meteors.remove(nextAngle)
            }
            pointer = nextAngle
        } else {
            pointer = Math.PI - DELTA
        }
    }
}