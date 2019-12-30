package nineteen

data class Point(val x: Long, val y: Long)

enum class Drone(val num: Long, val symbol: Char) {
    STATIONARY(0, '.'),
    PULLED(1, '#');

    companion object {
        fun fromLong(res: Long) = Drone.values().first { it.num == res }
    }
}

typealias Map = HashMap<Point, Drone>

data class BeamLine(val start: Point, val end: Point)

fun drawMap(map: Map, startPoint: Point, size: Long) {
    val drawing = (startPoint.y until startPoint.y + size).map { y ->
        (startPoint.x until startPoint.x + size).map { x ->
            map.getOrDefault(Point(x, y), Drone.STATIONARY).symbol
        }.toTypedArray() }.toTypedArray()

    val str = drawing.map { line ->
        line.joinToString("")
    }.joinToString("\n")
    println(str)
}

fun explore(program: Program, map: Map, startPoint: Point, size: Long) {
    var x = startPoint.x
    var y = startPoint.y
    while (y < startPoint.y + size) {
        val tile = tryPoint(program, Point(x, y))
        map[Point(x, y)] = tile

        x++
        if (x == startPoint.x + size) {
            x = startPoint.x
            y++
        }
    }
}

fun findBeamLine(program: Program, y: Long): BeamLine {
    var x = 0L

    var start: Point? = null
    var end: Point? = null
    while (end == null) {

        val tile = tryPoint(program, Point(x, y))

        when (tile) {
            Drone.STATIONARY -> {
                if (start != null) {
                    end = Point(x - 1, y)
                }
            }
            Drone.PULLED -> {
                if (start == null) {
                    start = Point(x, y)
                }
            }
        }

        x++
    }

    return BeamLine(start!!, end!!)
}

fun tryLine(program: Program, y: Long): List<Point> {
    val line = findBeamLine(program, y)

    val validPoints = mutableListOf<Point>()

    for (x in line.start.x..line.end.x) {
        val inFront = Point(x + 99, y)
        val below = Point(x, y + 99)

        val inFrontOk = tryPoint(program, inFront) == Drone.PULLED
        val belowOk = tryPoint(program, below) == Drone.PULLED

        if (!inFrontOk) {
            break
        }

        if (inFrontOk && belowOk) {
            validPoints.add(Point(x, y))
        }
    }

    println("Line $y with ${validPoints.size}, ${validPoints}")

    return validPoints
}

fun tryPoint(program: Program, point: Point): Drone {
    val programCopy = Program(program)

    val input = mutableListOf<Long>()
    input.add(point.x)
    input.add(point.y)

    val intcode = Intcode(programCopy, input, 0, 0, mutableListOf())

    intcode.run()

    return Drone.fromLong(intcode.output.removeAt(0))
}

fun find100x100Square(program: Program, searchRangeStart: Long, searchRangeEnd: Long): Point? {
    var low = searchRangeStart
    var high = searchRangeEnd

    var bestPointSoFar: Point? = null

    while (low+1 < high) {
        val aproximate = low + (high - low) / 2

        val line= findBeamLine(program, aproximate)

        val candidate = Point(line.end.x - 99, line.end.y)
        val oposite = Point(line.end.x - 99, line.end.y + 99)

        val candidateOk = tryPoint(program, candidate) == Drone.PULLED
        val opositeOk = tryPoint(program, oposite) == Drone.PULLED


        if (candidateOk && opositeOk) {
            bestPointSoFar = candidate
            high = aproximate
        } else {
            low = aproximate
        }

        println("Try $aproximate, beamLength=${line.end.x - line.start.x}, candidate=$candidateOk, oposite=$opositeOk, Range: ($low, $high)")
    }

    return bestPointSoFar
}

fun main() {
    val LIMIT: Long = 100000
    val PROGRAM: HashMap<Long, Long> = HashMap()
    (0..LIMIT).forEach { PROGRAM[it] = 0L }
    INPUT.forEachIndexed{ index, inp -> PROGRAM[index.toLong()] = inp }

    val map = Map()

    val startPoint = Point(0, 0)
    explore(PROGRAM, map, startPoint, 100)
    drawMap(map, startPoint, 100)

    println("==FIRST==")
    val pulledTilesCount = map.values.count { it == Drone.PULLED }
    println(pulledTilesCount)

    println("==SECOND==")
    // binary search
    val minPointForSquare = find100x100Square(PROGRAM, 800, 1600)
    println(minPointForSquare)
    if (minPointForSquare != null) {
        println(minPointForSquare.x * 10000 + minPointForSquare.y)

        // more trustworthy search but linear - slow
        (minPointForSquare.y - 10..minPointForSquare.y).forEach {
            tryLine(PROGRAM, it)
        }
    }
}

// 790108 - too low
// 7701053 - too high
// 7641045
// 7621042 correct