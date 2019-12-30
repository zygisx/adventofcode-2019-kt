package thrirteen

enum class TileType(val id: Long){
    EMPTY(0),
    WALL(1),
    BLOCK(2),
    HORIZONTAL_PADDLE(3),
    BALL(4);

    companion object {
        fun fromLong(id: Long) = TileType.values().first { it.id == id }
    }
}

data class Point(val x: Long, val y: Long)

data class Tile(val type: TileType, val p: Point)

fun first(PROGRAM: HashMap<Long, Long>) {
    val input = mutableListOf<Long>();
    val output = mutableListOf<Long>();

    val intcode = Intcode(PROGRAM, input, 0, 0, output)
    intcode.run()

    val tiles = ArrayList<Tile>()
    var i = 0
    while (i < intcode.output.size) {
        val x = intcode.output[i]
        val y = intcode.output[i+1]
        val tile = TileType.fromLong(intcode.output[i+2])

        tiles.add(Tile(tile, Point(x, y)))
        i += 3
    }


    val blocksCount = tiles.filter { it.type == TileType.BLOCK}.size
    println("===FIRST===")
    println("ANSWER: $blocksCount")
}

fun symbol(tile: TileType): Char {
    return when (tile) {
        TileType.WALL -> '|'
        TileType.HORIZONTAL_PADDLE -> '_'
        TileType.BALL -> '•'
        TileType.BLOCK -> '#'
        TileType.EMPTY -> ' '
    }
}

fun draw(tiles: HashMap<Point, Tile>) {
    // array[50][50]
    val drawing = (0..25).map { (0..40).map { ' ' }.toTypedArray() }.toTypedArray()

    tiles.values.forEach{ drawing[it.p.y.toInt()][it.p.x.toInt()] = symbol(it.type)}

    val str = (0..25).map { y ->
        (0..40).map { x ->
            drawing[y][x]
        }.joinToString("")
    }.joinToString("\n")
    println(str)
}

fun getInput(): Long {
    val input= readLine().orEmpty().removeSurrounding("\n")
    return when (input) {
        "1"  ->  -1L
        "2"  ->  0L
        "3"  ->  1L
        else -> 0L
    }
}

fun getPredictedInput(tiles: HashMap<Point, Tile>): Long {
    val  ball = tiles.values.findLast { it.type ==  TileType.BALL }!!
    val  padle = tiles.values.findLast { it.type ==  TileType.HORIZONTAL_PADDLE }!!
    if (padle.p.x < ball.p.x) return 1L
    if (padle.p.x > ball.p.x) return -1L
    return 0L
}

fun second(PROGRAM: HashMap<Long, Long>) {
    val input = mutableListOf<Long>()
    val output = mutableListOf<Long>()

    PROGRAM[0] = 2 // play it for free

    val intcode = Intcode(PROGRAM, input, 0, 0, output)

    val tiles = HashMap<Point, Tile>()

    val scores = ArrayList<Pair<Int, Long>>()
    val inputsHistory = ArrayList<Long>()
    inputsHistory.addAll(input)

    var score = 0L

    while (!intcode.dead) {
        intcode.run()

        var i = 0
        while (i < intcode.output.size) {
            val x = intcode.output[i]
            val y = intcode.output[i+1]
            val tileOrScore = intcode.output[i+2]

            if (x == -1L && y == 0L) {
                score = tileOrScore
            } else {
                val p = Point(x, y)
                tiles[p] = Tile(TileType.fromLong(tileOrScore), p)
            }
            i += 3
        }

        val tileCount = tiles.values.filter { it.type == TileType.BLOCK}.size

        println("\u001Bc")
        println("BLOCKS: $tileCount SCORE: $score")
        draw(tiles)

        Thread.sleep(10)
        val joystickPosition = getPredictedInput(tiles)
        intcode.output = mutableListOf()
        intcode.input.add(joystickPosition)
    }

    println(scores.distinct())

}

fun main() {
    val LIMIT: Long = 100000
    val PROGRAM: HashMap<Long, Long> = HashMap()
    (0..LIMIT).forEach { PROGRAM[it] = 0L }
    INPUT.forEachIndexed{ index, inp -> PROGRAM[index.toLong()] = inp }

//    first(PROGRAM)
    second(PROGRAM)
}