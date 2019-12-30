package eightteen

import java.util.*
import kotlin.collections.ArrayList
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

typealias Keyset = Set<Char>
typealias Map = HashMap<Point, Char>

fun isKey(tile: Char) = tile.isLowerCase()

fun possibleMoves(point: Point, map: Map, keyset: Keyset): List<Point> {
    return point.neighbours().filter {
        val tile = map[it]!!
        when (tile) {
            '.' -> true
            '#' -> false
            else ->
                if (tile.isUpperCase()) keyset.contains(tile.toLowerCase())
                else true

        }
    }
}

fun openMoves(point: Point, map: Map): List<Point> {
    return point.neighbours().filter {
        val tile = map[it]!!
        when (tile) {
            '#' -> false
            else -> true
        }
    }
}

fun parseMap(input: String): Map  {
    val map = Map()
    input.lines().forEachIndexed { y, line ->
        line.toCharArray().forEachIndexed{ x, char ->
            map[Point(x, y)] = char
        }
    }
    return map
}

data  class KeyPoint(val point: Point, val key: Char)

data class NextKey(val keyPoint: KeyPoint, val distance: Int)

data class Vector(val from: KeyPoint, val to: KeyPoint, val distance: Int, val keysNeeded: Keyset)


data class PointQueueItem(val point: Point, val distance: Int, val keysNeeded: Keyset)

fun findShortest(map: Map, start: Point, target: Char): Vector? {

    val pointsQueue: Queue<PointQueueItem> = LinkedList()
    val visitedIn: HashMap<Point, Int> = HashMap()

    pointsQueue.add(PointQueueItem(start, 0, HashSet()))

    while(pointsQueue.isNotEmpty()) {
        val item = pointsQueue.poll()!!

        visitedIn[item.point] = item.distance

        var newKeysNeeded = item.keysNeeded
        val tile = map[item.point]!!
        if (tile.isUpperCase()) {
            newKeysNeeded  = newKeysNeeded.plus(tile.toLowerCase())
        } else if (tile.isLowerCase() && tile == target) {
            val startTile = map[start]!!
            return Vector(KeyPoint(start, startTile), KeyPoint(item.point, tile), item.distance, item.keysNeeded)
        }

        val possibleMoves = openMoves(item.point, map)
                .filter { !visitedIn.containsKey(it) || visitedIn[it]!! > item.distance }

        possibleMoves.forEach {
            pointsQueue.add(PointQueueItem(it, item.distance + 1, newKeysNeeded))
        }
    }

    return null
}

fun buildGraph(map: Map): List<Vector> {
    val allKeys = map.values.filter { it.isLowerCase() }
    println(allKeys)

    fun findCoordinates(key: Char) = map.entries.find { it.value == key }!!.key

    val vectors = allKeys.map { start ->
        allKeys.filter { it != start }.map { target ->
            findShortest(map, findCoordinates(start), target)
        }.filterNotNull()
    }.flatten()

    val start = findCoordinates('@')
    val fromStart = allKeys.mapNotNull { findShortest(map, start, it) }

    return vectors.plus(fromStart)
}

fun findNextKeys(map: Map, start: Point, existingKeys: Keyset): List<NextKey> {

    val pointsQueue: Queue<Pair<Point, Int>> = LinkedList()
    val visitedIn: HashMap<Point, Int> = HashMap()
    val nextKeys = ArrayList<NextKey>()
    pointsQueue.add(start to 0)


    while(pointsQueue.isNotEmpty()) {
        val point = pointsQueue.poll()!!

        visitedIn[point.first] = point.second

        val possibleMoves = possibleMoves(point.first, map, existingKeys)
                .filter { !visitedIn.containsKey(it) || visitedIn[it]!! > point.second }

        possibleMoves.forEach {
            val tile = map[it]!!
            if (isKey(tile) && !existingKeys.contains(tile)) {
                nextKeys.add(NextKey(KeyPoint(it, tile), point.second + 1))
            } else {
                pointsQueue.add(it to point.second + 1)
            }
        }
    }

    return nextKeys
}

fun findShortestPathSteps(map: Map, totalKeys: Int): Int {
//    val nextKeys = findNextKeys(map, startPoint, keyset)
//    println(nextKeys)
//    println(nextKeys.size)

    var currentRecord = Int.MAX_VALUE

    fun findShortestRec(start: Point, keys: Keyset, stepsSoFar: Int): Int {
        if (keys.size == totalKeys) {
            println("Terminating with ${keys.size} got $stepsSoFar $currentRecord")
            currentRecord = Math.min(stepsSoFar, currentRecord)
            return stepsSoFar
        }

        if (stepsSoFar > currentRecord) {
            return stepsSoFar
        }

        val nextKeys = findNextKeys(map, start, keys).distinct()
        val shortestFromNextKeys = nextKeys.map { findShortestRec(it.keyPoint.point, keys.plus(it.keyPoint.key), stepsSoFar + it.distance) }
//        println(shortestFromNextKeys)
        return shortestFromNextKeys.min()!!
    }

    val startPoint = map.entries.first { it.value == '@' }.key

    return findShortestRec(startPoint, HashSet(), 0)
}

fun findNextVectors(graph: List<Vector>, start: Char, keys: Keyset): List<Vector> {
    val canVisit = graph
            .filter { start == it.from.key }  // vectors that starts from "start"
            .filter { keys.containsAll(it.keysNeeded) } // we have keys needed
            .filterNot { keys.contains(it.to.key) } // we are not  interested in keys we already have
    return canVisit
}

fun collectAllKeys(graph: List<Vector>): Int {
    val totalKeys = 26

    var currentRecord = Int.MAX_VALUE

    fun findShortestRec(start: Char, keys: Keyset, stepsSoFar: Int): Int {
        if (keys.size == totalKeys) {
            println("Terminating with ${keys.size} got $stepsSoFar $currentRecord")
            currentRecord = Math.min(stepsSoFar, currentRecord)
            return stepsSoFar
        }

        if (stepsSoFar > currentRecord) {
            return stepsSoFar
        }

        val nextVectors = findNextVectors(graph, start, keys)
        val allDistances = nextVectors.map { findShortestRec(it.to.key, keys.plus(it.to.key), stepsSoFar + it.distance) }
        return allDistances.min()!!
    }

    return findShortestRec('@', HashSet(), 0)
}

fun main() {
    val map = parseMap(INPUT)

    println(map.entries.find { it.value == '@' }!!.key)

    val totalKeys = map.values.filter { it.isLowerCase() }.size
    println("Looking for $totalKeys")

    val graph = buildGraph(map)
    println(graph)
    println(graph.size)

    val result = collectAllKeys(graph)
    println(result)
//    val result = findShortestPathSteps(map, totalKeys)
//    println(result)
}