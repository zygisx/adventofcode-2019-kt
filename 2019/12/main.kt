package twelve

import kotlin.math.absoluteValue

data class Velocity(var x: Int, var y: Int, var z: Int) {
    companion object {
        fun zero() = Velocity(0, 0, 0)
    }
}
data class Position(var x: Int, var y: Int, var z: Int)
data class Moon(var position: Position, var velocity: Velocity) {
    override fun toString(): String {
        return "pos=<x=${position.x}, y=${position.y}, z=${position.z}>, vel=<x=${velocity.x}, y=${velocity.y}, z=${velocity.z}>"
    }
}

data class MoonCoorrdinate(var position: Int, var velocity: Int)


val regex = Regex("\\<x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)\\>")

fun readInput(input: String): List<Position> {
    return input.lines().map {
        println(it)
        val match = regex.matchEntire(it)
        val groups = match!!.groupValues
        Position(groups[1].toInt(), groups[2].toInt(), groups[3].toInt())
    }
}

fun applyGravity(firstCoordinate: Int, others: List<Int>): Int {
    return others.map { secondCoordinate ->
        when {
            firstCoordinate < secondCoordinate -> 1
            secondCoordinate < firstCoordinate -> -1
            else -> 0
        }
    }.sum()
}


fun simulate(moons: List<Moon>): List<Moon> {
    // apply gravity
    val velocities= moons.mapIndexed {idx, moon ->
        val others = moons.filter { it != moon }
//        println("$idx: ${moon.position.x} ${others.map { it.position.x }}| ${applyGravity(moon.position.x, others.map { it.position.x })}")
        Velocity(
                moon.velocity.x + applyGravity(moon.position.x, others.map { it.position.x }),
                moon.velocity.y + applyGravity(moon.position.y, others.map { it.position.y }),
                moon.velocity.z + applyGravity(moon.position.z, others.map { it.position.z })
            )
    }

    moons.zip(velocities).forEach { it.first.velocity = it.second }

    // apply velocity

    val positions = moons.map { moon ->
        Position(
        moon.position.x + moon.velocity.x,
        moon.position.y + moon.velocity.y,
        moon.position.z + moon.velocity.z
        )
    }

    moons.zip(positions).forEach { it.first.position = it.second }

    return moons
}

fun potentialEnergies(moons: List<Moon>): List<Int> {
    return moons.map {
        it.position.x.absoluteValue + it.position.y.absoluteValue + it.position.z.absoluteValue
    }
}

fun kineticEnergies(moons: List<Moon>): List<Int> {
    return moons.map {
        it.velocity.x.absoluteValue + it.velocity.y.absoluteValue + it.velocity.z.absoluteValue
    }
}

fun simulateSingle(moons: List<MoonCoorrdinate>): List<MoonCoorrdinate> {
    // apply gravity
    val velocities= moons.mapIndexed {idx, moon ->
        val others = moons.filter { it != moon }
            moon.velocity + applyGravity(moon.position, others.map { it.position })
    }

    moons.zip(velocities).forEach { it.first.velocity = it.second }

    // apply velocity

    val positions = moons.map { moon -> moon.position + moon.velocity }

    moons.zip(positions).forEach { it.first.position = it.second }

    return moons
}

fun findRepeatTime(moons: List<MoonCoorrdinate>): Int {
    fun hashFunction(moons: List<MoonCoorrdinate>):String {
        fun hashMoon(moon: MoonCoorrdinate) = "${moon.position}_${moon.velocity}"
        val str = moons.joinToString("|") { hashMoon(it) }
        return str
    }

    var moonsOn = moons

    val foundRegistry = HashMap<String, Boolean>()
    var time = 0
    while (true) {
        time++
        moonsOn = simulateSingle(moonsOn)
        val hash = hashFunction(moonsOn)
        if (foundRegistry.containsKey(hash)) {
            break
        } else {
            foundRegistry[hash] = true
        }

    }

    return time - 1
}

fun lcm(xTime: Long, yTime: Long, zTime: Long): Long {
    fun gcd(a: Long, b: Long): Long {
        if (a == 0L) return b
        return gcd(b % a, a)
    }

    fun lcm(a: Long,b: Long): Long {
        return (a*b)/gcd(a, b)
    }

    val xAndY = lcm(xTime, yTime)
    println(xAndY)
    return lcm(xAndY, zTime)
}

fun main() {
    val positions = readInput(INPUT)

    var moons = positions.map { Moon(it, Velocity.zero()) }
    moons.forEach { println(it) }

    (1..1000).forEach {
        moons = simulate(moons)
    }
    val pot = potentialEnergies(moons)
    val kin = kineticEnergies(moons)

    val result = pot.zip(kin).map { it.first * it.second}.sum()
    println("===First===")
    println(result)



    val xMoons = moons.map { MoonCoorrdinate( it.position.x, it.velocity.x) }
    val xTime = findRepeatTime(xMoons)
    val yMoons = moons.map { MoonCoorrdinate( it.position.y, it.velocity.y) }
    val yTime = findRepeatTime(yMoons)
    val zMoons = moons.map { MoonCoorrdinate( it.position.z, it.velocity.z) }
    val zTime = findRepeatTime(zMoons)
    println("Xtime: $xTime")
    println("Ytime: $yTime")
    println("Ztime: $zTime")

    val result2nd = lcm(xTime.toLong(), yTime.toLong(), zTime.toLong())
    println("===Second===")
    println(result2nd)


    // 425229394117455 - too low
    // 3826909308673605
}