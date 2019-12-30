package fifteen

import java.util.*
import kotlin.collections.HashMap


enum class Direction(val direction: Long) {
    NORTH(1),
    SOUTH(2),
    WEST(3),
    EAST(4);

    companion object {
        fun fromLong(dir: Long) = Direction.values().first { it.direction == dir }
    }
}

enum class DroidResponse(val response: Long) {
    WALL(0),
    MOVED(1),
    OXYGEN_SYSTEM(2);

    companion object {
        fun fromLong(res: Long) = DroidResponse.values().first { it.response == res }
    }
}

data class Point(val x: Long, val y: Long) {

    fun  move(direction: Direction): Point {
        return when(direction) {
            Direction.NORTH -> Point(this.x, this.y - 1)
            Direction.SOUTH -> Point(this.x, this.y + 1)
            Direction.WEST -> Point(this.x - 1, this.y)
            Direction.EAST -> Point(this.x + 1, this.y)
        }
    }
}

fun  explore() {

}

fun symbol(tile: DroidResponse): Char {
    return when (tile) {
        DroidResponse.WALL -> '#'
        DroidResponse.MOVED -> '.'
        DroidResponse.OXYGEN_SYSTEM -> 'Ø'
    }
}

val border =  40
val startPoint = Point(20, 20)

fun draw(map: HashMap<Point, DroidResponse>) {
    // array[50][50]
    val drawing = (0..border).map { (0..border).map { ' ' }.toTypedArray() }.toTypedArray()

    map.forEach{ drawing[it.key.y.toInt()][it.key.x.toInt()] = symbol(it.value)}

    drawing[20][20] = 'S'

    val str = (0..border).map { y ->
        (0..border).map { x ->
            drawing[y][x]
        }.joinToString("")
    }.joinToString("\n")
    println(str)
}

fun getPossibleMoves(map: HashMap<Point, DroidResponse>, from: Point): List<Pair<Direction, Point>> {
    return  listOf(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
            .map { it to from.move(it) }
            .filter { it.second.x in 0..border && it.second.y in 0..border }
            .filter { map[it.second] != DroidResponse.WALL }
}

fun exploreMap(PROGRAM: HashMap<Long, Long>): HashMap<Point, DroidResponse> {
    val input = mutableListOf<Long>()
    val output = mutableListOf<Long>()
    val intcode = Intcode(PROGRAM, input, 0, 0, output)

    val map = HashMap<Point, DroidResponse>()
    val visits = HashMap<Point, Long>()

    var point = startPoint

    while (!intcode.dead) {
        val possibleMoves = getPossibleMoves(map, point)

        if (map.keys.size >= 1588) { // approximated value to cover all map
            break
        }

//        if (possibleMoves.all { visits.getOrDefault(it.second, 0) > 20 }) {
//            break
//        }

        if (possibleMoves.isEmpty()) {
            println("NO MORE POSSIBLE MOVES!!!")
            break
        }

        val unseenPoint = possibleMoves.find { !map.containsKey(it.second) }
        val leastVisited = possibleMoves.minBy { visits.getOrDefault(it.second, 0) }

        var chosenDirection: Pair<Direction, Point>

        chosenDirection = if (unseenPoint != null) {
            // prefer unseen
            unseenPoint
        } else {
            // if no unseen prefer least visited
            leastVisited!!
        }

        val newPoint = point.move(chosenDirection.first)

        intcode.input.add(chosenDirection.first.direction)
        intcode.run()

        val out = intcode.output.removeAt(0)
        val response = DroidResponse.fromLong(out)
        map[newPoint] = response
        if (response != DroidResponse.WALL) {
            visits[point] = visits.getOrDefault(point, 0) + 1
            point = newPoint
        }
    }
    return map
}

fun bfs(map: HashMap<Point, DroidResponse>, start: Point, target: Point): Int {

    val pointsQueue: Queue<Pair<Point, Int>> = LinkedList()
    val visitedIn: HashMap<Point, Int> = HashMap()
    pointsQueue.add(start to 0)


    while(pointsQueue.isNotEmpty()) {
        val point = pointsQueue.poll()!!

        visitedIn[point.first] = point.second

        val possibleMoves = getPossibleMoves(map, point.first)
                .map { it.second }
                .filter { !visitedIn.containsKey(it) || visitedIn[it]!! > point.second }

        possibleMoves.forEach {
            if (it == target) {
                return point.second + 1
            } else {
                pointsQueue.add(it to point.second + 1)
            }
        }
    }

    return -1
}

fun timeToFillOxygen(map: HashMap<Point, DroidResponse>, oxygenStation: Point): Int {

    fun hasNonOxygenPoints() = map.values.any { it == DroidResponse.MOVED }

    var time = 0
    while(hasNonOxygenPoints()) {

        map
            .filter { it.value == DroidResponse.OXYGEN_SYSTEM }
            .map { getPossibleMoves(map, it.key) }
            .flatten()
            .forEach { map[it.second] = DroidResponse.OXYGEN_SYSTEM }
//
        time++ // one minute passed
    }

    return time
}

fun main() {
    val LIMIT: Long = 100000
    val PROGRAM: HashMap<Long, Long> = HashMap()
    (0..LIMIT).forEach { PROGRAM[it] = 0L }
    INPUT.forEachIndexed{ index, inp -> PROGRAM[index.toLong()] = inp }


    val map = exploreMap(PROGRAM)

    draw(map)

    val oxygenSystem = map.entries.find { it.value == DroidResponse.OXYGEN_SYSTEM }!!.key
    val movesNeeded = bfs(map, startPoint, oxygenSystem)

    println("=====FIRST=====")
    println(movesNeeded)

    println("=====SECOND=====")
    println(timeToFillOxygen(map, oxygenSystem))

    draw(map)
}