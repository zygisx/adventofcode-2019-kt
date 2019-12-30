package twenty

import five.getArgument
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.HashSet


data class Point(val x: Int, val y: Int) {
    fun neighbours(): List<Point> {
        return listOf(
                Point(this.x, this.y - 1),
                Point(this.x, this.y + 1),
                Point(this.x - 1, this.y),
                Point(this.x + 1, this.y)
        )
    }
}

typealias Map = HashMap<Point, String>
data  class Portal(val point: Point, val name: String)
data class Vector(val from: Portal, val to: Portal, val distance: Int, val direct: Boolean)

fun toMap(input: String): Map {

    val map = Map()

    val lines = input.lines().filter { it.isNotBlank() }


    val firstMapLine = lines[2]
    val firstMapColumn = lines.map { line -> line[2] }

    val start = 2
    val end = firstMapLine.length - 2

    val top  = 2
    val bottom  = firstMapColumn.size - 2
    
    println(firstMapColumn.size)


    // initial map
    val chars = lines.subList(top, bottom).map { it.substring(start, end) }
    chars.forEachIndexed { y, line -> 
        line.forEachIndexed{ x, symbol ->
            if (symbol == '#' || symbol == '.') {
                map[Point(x, y)] = symbol.toString() 
            }
        }
    }

    val columnsSize = end - start
    val linesSize = bottom - top
    
    // outer ports

    val outerTop1 = lines[0]
    val outerTop2 = lines[1]

    outerTop1.zip(outerTop2).forEachIndexed { idx, symbols ->
        if (symbols.first.isUpperCase() && symbols.second.isUpperCase()) {
            map[Point(idx - start, 0)] = "${symbols.first}${symbols.second}O"
        }
    }


    val outerBottom1 = lines.takeLast(2)[0]
    val outerBottom2 = lines.last()

    outerBottom1.zip(outerBottom2).forEachIndexed { idx, symbols ->
        if (symbols.first.isUpperCase() && symbols.second.isUpperCase()) {
            map[Point(idx - start, bottom - top - 1)] = "${symbols.first}${symbols.second}O"
        }
    }

    val outerLeft1 = lines.map { it[0] }
    val outerLeft2 = lines.map{ it[1] }

    outerLeft1.zip(outerLeft2).forEachIndexed { idx, symbols ->
        if (symbols.first.isUpperCase() && symbols.second.isUpperCase()) {
            map[Point(0, idx - top)] = "${symbols.first}${symbols.second}O"
        }
    }


    val outerRight1 = lines.map { it.getOrElse(end){' '} }
    val outerRight2 = lines.map{ it.getOrElse(end+1){' '} }

    outerRight1.zip(outerRight2).forEachIndexed { idx, symbols ->
        if (symbols.first.isUpperCase() && symbols.second.isUpperCase()) {
            map[Point(end - start - 1, idx - top)] = "${symbols.first}${symbols.second}O"
        }
    }
    
    // inner ports
    val middleLine = lines[(bottom - top) / 2].substring(start, end)
    val innerStart = middleLine.indexOfFirst { it == ' ' || it.isUpperCase() }
    val innerEnd = middleLine.indexOfLast { it == ' ' || it.isUpperCase() } + 1 
    
    val middleColumnIdx = (end - start) / 2
    val middleColumn = lines.map { line -> line[middleColumnIdx] }.subList(top, bottom)
    
    val innerTop = middleColumn.indexOfFirst { it == ' ' || it.isUpperCase() }
    val innerBottom = middleColumn.indexOfLast { it == ' ' || it.isUpperCase() } + 1

    val innerTop1 = lines[top + innerTop].substring(start + innerStart, start + innerEnd)
    val innerTop2 = lines[top + innerTop + 1].substring(start + innerStart, start + innerEnd)
    innerTop1.zip(innerTop2).forEachIndexed { idx, symbols ->
        if (symbols.first.isUpperCase() && symbols.second.isUpperCase()) {
            map[Point(innerStart+idx, innerTop - 1)] = "${symbols.first}${symbols.second}I"
        }
    }

    val innerBottom1 = lines[top + innerBottom-2].substring(start + innerStart, start + innerEnd)
    val innerBottom2 = lines[top + innerBottom-1].substring(start + innerStart, start + innerEnd)
    innerBottom1.zip(innerBottom2).forEachIndexed { idx, symbols ->
        if (symbols.first.isUpperCase() &&symbols.second.isUpperCase()) {
            map[Point(innerStart+idx, innerBottom)] = "${symbols.first}${symbols.second}I"
        }
    }

    val innerLeft1 = lines.map { it.getOrElse(start + innerStart){' '} }.subList(top + innerTop, top + innerBottom)
    val innerLeft2 = lines.map { it.getOrElse(start + innerStart + 1){' '} }.subList(top + innerTop, top + innerBottom)
    innerLeft1.zip(innerLeft2).forEachIndexed { idx, symbols ->
        if (symbols.first.isUpperCase() &&symbols.second.isUpperCase()) {
            map[Point(innerStart - 1, innerTop + idx)] = "${symbols.first}${symbols.second}I"
        }
    }

    val innerRight1 = lines.map { it.getOrElse(start + innerEnd - 2){' '} }.subList(top + innerTop, top + innerBottom)
    val innerRight2 = lines.map { it.getOrElse(start + innerEnd - 1){' '} }.subList(top + innerTop, top + innerBottom)
    println(innerRight1)
    println(innerRight2)
    innerRight1.zip(innerRight2).forEachIndexed { idx, symbols ->
        if (symbols.first.isUpperCase() &&symbols.second.isUpperCase()) {
            map[Point(innerEnd, innerTop + idx)] = "${symbols.first}${symbols.second}I"
        }
    }
    
    println("$innerStart $innerEnd $innerTop $innerBottom")


    return map
}

fun drawMap(map: Map) {
    val width = map.map { it.key.x }.max()!!
    val height = map.map { it.key.y }.max()!!
    
    println(width)
    println(height)

    val str = (0..height).map { y ->
        (0..width).map { x ->
            map.getOrDefault(Point(x, y), " ")[0]
        }.joinToString("")
    }.joinToString("\n")

    println(str)
}

data class PointQueueItem(val point: Point, val distance: Int)

fun openMoves(point: Point, map: Map): List<Point> {
    return point.neighbours().filter {
        val tile = map[it]
        when (tile) {
            null -> false
            "#" -> false
            " " -> false
            "." -> true
            else -> {
                true
            }
        }
    }
}

fun findShortest(map: Map, start: Point, target: Point): Vector? {
    val startTile = map[start]!!
    val targetTile = map[target]!!
    if (startTile.substring(0..1) == targetTile.substring(0..1)) {
        return twenty.Vector(Portal(start, startTile), Portal(target, targetTile), 1, true)
    }

    val pointsQueue: Queue<PointQueueItem> = LinkedList()
    val visitedIn: HashMap<Point, Int> = HashMap()

    pointsQueue.add(PointQueueItem(start, 0))

    while(pointsQueue.isNotEmpty()) {
        val item = pointsQueue.poll()!!

        visitedIn[item.point] = item.distance

        val tile = map[item.point]!!
        
        if (item.point == target) {
            return Vector(Portal(start, startTile), Portal(item.point, tile), item.distance, false)
        }

        val possibleMoves = openMoves(item.point, map)
                .filter { !visitedIn.containsKey(it) || visitedIn[it]!! > item.distance }

        possibleMoves.forEach {
            pointsQueue.add(PointQueueItem(it, item.distance + 1))
        }
    }

    return null
}

fun buildGraph(map: Map): List<Vector> {
    val allPortals = map.filterValues { it.length == 3 }

    val vectors = allPortals.map { start ->
        allPortals.filter { it != start }.map { target ->
            findShortest(map, start.key, target.key)
        }.filterNotNull()
    }.flatten()

    return vectors
}

fun findNextVectors(graph: List<Vector>, start: Point, alreadyVisited: Set<Point>): List<Vector> {
    val canVisit = graph
            .filter { start == it.from.point }  // vectors that starts from "start"
            .filterNot { alreadyVisited.contains(it.to.point) } // we are not  interested in already visited
    return canVisit
}

fun shortestPath(graph: List<Vector>, entry: Point, target: Point): Int {
    var currentRecord = Int.MAX_VALUE

    fun findShortestRec(start: Point, stepsSoFar: Int, alreadyVisited: Set<Point>): Int {
        if (start == target) {
            println("Terminating with $stepsSoFar $currentRecord")
            currentRecord = Math.min(stepsSoFar, currentRecord)
            return stepsSoFar
        }

        if (stepsSoFar > currentRecord) {
            return stepsSoFar
        }

        val nextVectors = findNextVectors(graph, start, alreadyVisited)
        val visitedSet = alreadyVisited.plus(start)
        val allDistances = nextVectors.map { findShortestRec(it.to.point, stepsSoFar + it.distance, visitedSet) }
        return allDistances.min() ?: Int.MAX_VALUE
    }


    return findShortestRec(entry, 0,  HashSet())
}


data class Visited(val point: Point, val level: Int)

fun findNextVectors2(graph: List<Vector>, start: Point, currentLevel: Int, alreadyVisited: Set<Visited>): List<Vector> {
    val canVisit = graph
            .filter { start == it.from.point }  // vectors that starts from "start"
            .filterNot { it.to.name == "ZZO" && currentLevel > 0 }
            .filterNot {
                var newLevel = currentLevel
                if (it.direct) {
                    newLevel = when (it.to.name.last()) {
                        'I' -> currentLevel - 1
                        'O' -> currentLevel + 1
                        else -> throw IllegalArgumentException("kas cia: ${it.to}")
                    }
                    if (it.to.name == "ZZO") {
                        newLevel = currentLevel
                    }
                    
                }
                alreadyVisited.contains(Visited(it.to.point, newLevel))
            } // we are not  interested in already visited
    return canVisit
}

fun shortestPath2(graph: List<Vector>, entry: Point, target: Point): Int {
    var currentRecord = Int.MAX_VALUE

    fun findShortestRec(start: Point, stepsSoFar: Int, level: Int, alreadyVisited: Set<Visited>): Int {
        if (start == target && level == 0 ) {
            println("Terminating with $stepsSoFar $currentRecord")
            currentRecord = Math.min(stepsSoFar, currentRecord)
            return stepsSoFar
        }

        if (stepsSoFar > currentRecord) {
//            println("Too much: $stepsSoFar in lvl $level")
            return stepsSoFar
        }

        var nextVectors = findNextVectors2(graph, start, level, alreadyVisited)
        // From zero only can go inside
        if (level == 0) {
//            nextVectors = nextVectors.filter { it.to.name.last() == 'I' }
        }
        
//        println("level: $level $stepsSoFar")
        
        print("$start.->")
        val visitedSet = alreadyVisited.plus(Visited(start, level))
        val allDistances = nextVectors.map {
            var newLevel = level
            if (it.direct) {
                newLevel =  when (it.to.name.last()) {
                    'I' -> level - 1
                    'O' -> level + 1
                    else -> throw IllegalArgumentException("kas cia: ${it.to}")
                }
            }
            findShortestRec(it.to.point, stepsSoFar + it.distance, newLevel, visitedSet)
        }
        return allDistances.min() ?: Int.MAX_VALUE
    }


    return findShortestRec(entry, 0,  0, HashSet(setOf(Visited(entry, 0))))
}


fun main() {
    val map = toMap(INPUT)
//    println(map)
    drawMap(map)
    
    val target = map.filter { it.value == "ZZO" }.toList().first().first
    val entry = map.filter { it.value == "AAO" }.toList().first().first
    val  graph = buildGraph(map)
    println(graph)
    
    println(graph.filter { it.from.name == "AAO" })
    
    val path = shortestPath2(graph, entry, target)
    println(path)
}

// 1165 - too low
// 6642