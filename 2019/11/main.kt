package eleven

enum class Color(val c: Int) {
    BLACK(0),
    WHITE(1)
}

enum class Turn(val c: Int) {
    LEFT(0),
    RIGHT(1)
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

data class Point(val x: Int, val y: Int) {
    fun move(direction: Direction) =
        Point(this.x + direction.x, this.y + direction.y)
}

fun main() {

    val LIMIT: Long = 100000
    val PROGRAM: HashMap<Long, Long> = HashMap()
    (0..LIMIT).forEach { PROGRAM[it] = 0L }
    INPUT.forEachIndexed{ index, inp -> PROGRAM[index.toLong()] = inp }



    var input = mutableListOf<Long>()
    val intcode = Intcode(PROGRAM, input, 0, 0)
    var point = Point(0, 0)
    var direction = Direction.UP

    val drawing = HashMap<Point, Char>()
    drawing[Point(0, 0)] = '#'

    while (!intcode.dead) {
        if (drawing.containsKey(point)) {
            println("DOUBLE PAINT $point")
        }
        val color = if (drawing.containsKey(point)) drawing[point] else '.'
        when (color) {
            '.' -> input = mutableListOf<Long>(0)
            '#' -> input = mutableListOf<Long>(1)
            else -> println("ERROR!")
        }

        intcode.input = input
        intcode.output = mutableListOf()
        intcode.run()
        if (intcode.dead) {
            break
        }

        val out = intcode.output

        val newColor = out[0]
        val newDirection = out[1]

        when (newColor) {
            0L -> drawing[point] = '.' // BLACK
            1L -> drawing[point] = '#' // WHITE
            else -> println("COLOR ERROR")
        }

        when (newDirection) {
            0L -> direction = direction.left()
            1L -> direction = direction.right()
            else -> println("DIRECTION ERROR")
        }

        point = point.move(direction)
    }

    println(drawing.keys.size)

    val str = (0 until 50).map { y ->
        (0 until 50).map { x ->
            val p = Point(x, y)
            if (drawing.containsKey(p) && drawing[p] == '.') ' ' else '█'
        }.joinToString("")
    }.joinToString("\n")

    println(str)
}