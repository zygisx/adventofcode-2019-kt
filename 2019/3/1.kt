package three

import java.lang.IllegalArgumentException

data class Path(val direction: String, val distance: Int)

data class Coordinate(val x: Int, val y: Int) {
    fun move(direction: Direction): Coordinate = Coordinate(this.x + direction.x, this.y +  direction.y)
}

data class Direction(val x: Int, val y: Int) {

    companion object {
        fun fromString(direction: String): Direction =
            when (direction) {
                "U" -> Direction(0, 1)
                "D" -> Direction(0, -1)
                "R" -> Direction(1, 0)
                "L" -> Direction(-1, 0)
                else -> throw IllegalArgumentException()
            }
    }
}

enum class Visit {
    First, Second, Crossed
}

data class MapPoint(var visit: Visit, var steps: HashMap<Visit, Int>)

typealias Map = HashMap<Coordinate, MapPoint>

fun parseInput(line: String): List<Path> =
    line
        .split(",")
        .map { Path(it.substring(0..0),  it.substring(1).toInt()) }



fun putWireIntoMap(paths: List<Path>, map: Map, visitor: Visit): Map {
    var pointer = Coordinate(0, 0)
    var stepsCounter = 0
    paths.forEach{
        val direction = Direction.fromString(it.direction)
        (0 until it.distance).forEach{
            stepsCounter++
            pointer = pointer.move(direction)
            if (map.containsKey(pointer) && map[pointer]!!.visit != visitor) {
                map[pointer]!!.visit = Visit.Crossed
                map[pointer]!!.steps[visitor] = stepsCounter
            }  else {
                map[pointer] = MapPoint(visitor, HashMap())
                map[pointer]!!.steps[visitor] = stepsCounter
            }
        }
    }

    return map
}

// 1st
fun findClosesCrossedCoordinate(map: Map): Coordinate {
    val entry = map
        .filterValues { it.visit == Visit.Crossed }
        .minBy { Math.abs(it.key.x) + Math.abs(it.key.y) }
    println(map.filterValues { it.visit == Visit.Crossed })
    return entry!!.key
}

// 2nd
fun findDistanceToNearest(map: Map): MapPoint {
    val entry = map
            .filterValues { it.visit == Visit.Crossed }
            .minBy { it.value.steps[Visit.First]!! + it.value.steps[Visit.Second]!! }
    println(map.filterValues { it.visit == Visit.Crossed })
    return entry!!.value
}


fun main() {
    val line1 = parseInput(LINE1)
    val line2 = parseInput(LINE2)

    var wires = Map()
    wires = putWireIntoMap(line1, wires, Visit.First)
    wires = putWireIntoMap(line2, wires, Visit.Second)

    val closest = findClosesCrossedCoordinate(wires)
    println(Math.abs(closest.x) + Math.abs(closest.y))

    val nearest = findDistanceToNearest(wires)
    println(nearest.steps[Visit.First]!! + nearest.steps[Visit.Second]!!)
//    println(line1)
//    println(wires)
}