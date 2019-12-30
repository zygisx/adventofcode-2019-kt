package seventeen

import java.lang.RuntimeException


enum class CameraOutput(val symbol: Char, val num: Long) {
    SCAFFOLD('#', 35),
    SPACE('.', 46),
    NEWLINE('\n', 10),
    UP('^', 94);

    companion object {
        fun fromLong(res: Long) = CameraOutput.values().first { it.num == res }
    }
}

enum class Move(val symbol: Char, val num: Long) {
    A('A', 65),
    B('B', 66),
    C('C', 67),
    RIGHT('R', 82),
    LEFT('L', 76),
    COMMA(',', 44),
    NEWLINE('\n', 10);

    companion object {
        fun fromChar(symbol: Char) = Move.values().first { it.symbol == symbol }
    }
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, -1),
    LEFT(-1, 0),
    DOWN(0, 1),
    RIGHT(1, 0);

    fun right(): Direction {
        return when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }

    fun left(): Direction {
        return when (this) {
            UP -> LEFT
            LEFT -> DOWN
            DOWN -> RIGHT
            RIGHT -> UP
        }
    }
}
data class Point(val x: Long, val y: Long) {
    fun move(direction: Direction) =
            Point(this.x + direction.x, this.y + direction.y)
}


fun createMap(program: HashMap<Long, Long>): HashMap<Point, CameraOutput> {
    val input = mutableListOf<Long>()
    val output = mutableListOf<Long>()
    val intcode = Intcode(program, input, 0, 0, output)

    intcode.run()

    val allowed = CameraOutput.values().map { it.num }.toHashSet()
    println(intcode.output.filter { !allowed.contains(it) })

    val cameraOutputs = intcode.output.map { CameraOutput.fromLong(it) }

    val drawing = cameraOutputs.map{ it.symbol }.joinToString("")

    println(drawing)

    val map = HashMap<Point, CameraOutput>()
    var y = 0L
    var x = 0L
    cameraOutputs.forEach {
        when (it) {
            CameraOutput.NEWLINE -> {
                y++
                x = 0
            }
            else -> {
                map[Point(x, y)] = it
                x++
            }
        }
    }

    return map
}

fun findIntersections(map: HashMap<Point, CameraOutput>): List<Point> {
    val allDirections = arrayListOf(Point(0, -1), Point(1, 0), Point(0, 1), Point(-1, 0))
    return map
        .filter { it.value == CameraOutput.SCAFFOLD }
        .filter {
            val point = it.key
            // SCAFFOLD  in all directions
            allDirections.map { map[Point(point.x + it.x, point.y + it.y)] }.all { it == CameraOutput.SCAFFOLD }
        }.map { it.key }
}

fun findPath(map: HashMap<Point, CameraOutput>, start: Point): List<Pair<Move, Int>> {

    fun testPoint(point: Point): Boolean {
        return map.containsKey(point) && map[point] == CameraOutput.SCAFFOLD
    }

    fun findDirection(point: Point, direction: Direction): Pair<Move, Direction>? {
        if (testPoint(point.move(direction.left()))) {
            return Move.LEFT to direction.left()
        }
        if (testPoint(point.move(direction.right()))) {
            return Move.RIGHT to direction.right()
        }
        return null
    }

    val path = ArrayList<Pair<Move, Int>>()
    val startDirection = Direction.UP
    var currentPoint = start
    var newDirection = findDirection(start, startDirection)
    while (newDirection != null) {

        var steps = 0
        while (testPoint(currentPoint.move(newDirection.second))) {
            currentPoint = currentPoint.move(newDirection.second)
            steps++
        }

        path.add(newDirection.first to steps)

        newDirection = findDirection(currentPoint, newDirection.second)
    }

    return path
}

fun main() {
    val LIMIT: Long = 100000
    val PROGRAM: HashMap<Long, Long> = HashMap()
    (0..LIMIT).forEach { PROGRAM[it] = 0L }
    INPUT.forEachIndexed{ index, inp -> PROGRAM[index.toLong()] = inp }


    val map = createMap(PROGRAM)
    val intersections = findIntersections(map)
    val alignmentParameter = intersections.map{ it.x * it.y}.sum()

    println("==FIRST==")
    println(alignmentParameter)

    println("==SECOND==")

    val startPoint = map.filter { it.value == CameraOutput.UP }.keys.first()
    val path  = findPath(map, startPoint)

    val stringPath = path.joinToString(Move.COMMA.symbol.toString()) { "${it.first.symbol}${it.second}" }
    println(stringPath)

//    val zipMap = HashMap<Move, List<Pair<Move, Int>>>()
//    (20 downTo 2).forEach{len ->
//        val sub = path.subList(0, len)
//
//        val goodStarts = (len+1 until path.size-len).filter { start ->
//            sub == path.subList(start, len)
//        }
//    }


    // restart program
    PROGRAM.clear()
    (0..LIMIT).forEach { PROGRAM[it] = 0L }
    INPUT.forEachIndexed{ index, inp -> PROGRAM[index.toLong()] = inp }
    PROGRAM[0] = 2

    val routineInput = "A,A,B,C,B,A,C,B,C,A".toCharArray().map { Move.fromChar(it) }.map { it.num }
    val AInput = "L,6,R,12,L,6,L,8,L,8".toCharArray().map { it.toInt().toLong() }
    val BInput = "L,6,R,12,R,8,L,8".toCharArray().map { it.toInt().toLong() }
    val CInput = "L,4,L,4,L,6".toCharArray().map { it.toInt().toLong() }

    val input = mutableListOf<Long>()
    val output = mutableListOf<Long>()

    input.addAll(routineInput)
    input.add(Move.NEWLINE.num)
    input.addAll(AInput)
    input.add(Move.NEWLINE.num)
    input.addAll(BInput)
    input.add(Move.NEWLINE.num)
    input.addAll(CInput)
    input.add(Move.NEWLINE.num)
    input.add('y'.toInt().toLong())
    input.add(Move.NEWLINE.num)
    val intcode = Intcode(PROGRAM, input, 0, 0, output)

    intcode.run()

    println(intcode.output)
}