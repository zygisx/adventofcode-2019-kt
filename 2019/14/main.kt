package fourteen

import java.util.*
import kotlin.collections.HashMap

data class Product(val product: String, val amount: Long)

data class Reaction(val input: List<Product>, val output: Product)

val REACTION_REGEX = "(\\d+) ([A-Z]+)".toRegex()

fun toProduct(plain: String): Product {
    val match  = REACTION_REGEX.find(plain)

    val amount = match!!.groupValues[1]
    val product = match.groupValues[2]
    return Product(product, amount.toLong())
}

fun parseReactions(inp: String): List<Reaction> {
    val lines = inp.split("\n")

    return lines.map{
        val io = it.split("=>")
        val input = io[0]
        val output = io[1]

        val inputProducts = input.split(",")
                .map { it.removeSurrounding(" ") }
                .map { toProduct(it)}
        val outputProduct = toProduct(output)

        Reaction(inputProducts, outputProduct)
    }
}

fun neededMaterials(reactions: List<Reaction>, aldearyHave: HashMap<String, Long>) {
    val fuelNeeds = reactions.findLast { it.output.product == "FUEL" }!!.input
}

fun findOreNeeded(reactions: List<Reaction>, fuelCount: Long): Long {
    val queue: Queue<Product> = LinkedList()
    val leftovers = HashMap<String, Long>()
    var oreUsed = 0L

    queue.add(Product("FUEL", fuelCount))
    while (queue.isNotEmpty()) {
        val target = queue.remove()


        if (target.product == "ORE") {
            oreUsed += target.amount
            continue
        }

        val leftoverAmount = leftovers.getOrDefault(target.product, 0)
        val reaction = reactions.find { it.output.product == target.product }!!
        val amountNeeded = if (leftoverAmount > target.amount) 0 else target.amount - leftoverAmount
        val leftoverUsed =  if (leftoverAmount > target.amount) target.amount else leftoverAmount
        leftovers[target.product] = leftoverAmount - leftoverUsed

        val reactionProducedAmount = reaction.output.amount

        val times =
                if (amountNeeded % reactionProducedAmount == 0L) amountNeeded / reactionProducedAmount
                else amountNeeded / reactionProducedAmount + 1

        if (reactionProducedAmount * times > amountNeeded) {
            leftovers[target.product] = leftovers.getOrDefault(target.product, 0) + (reactionProducedAmount * times - amountNeeded)
        }

        reaction.input.forEach {
            queue.add(Product(it.product, it.amount * times))
        }
    }

    return oreUsed
}

fun main() {
    val reactions = parseReactions(INPUT)

//    println(reactions.map { it.output.product }.size)
//    println(reactions.map { it.output.product }.distinct().size)

    println("===FIRST===")
    val orePerFuel = findOreNeeded(reactions, 1)
    println("ORE PER FUEL: ${orePerFuel}")


    // 2nd bin search

    val trillion = 1000000000000L
    val RIGHT = 2000000L
    val LEFT = 1000000L

    var right = RIGHT
    var left = LEFT
    while (left + 1 < right) {
        val mid = (right + left) / 2
        val oreNeeded = findOreNeeded(reactions, mid)

        if (oreNeeded > trillion) {
            right = mid
        } else if (oreNeeded < trillion) {
            left = mid
        } else {
            break
        }

        println("[$left, $right]")
    }

    println(findOreNeeded(reactions, left))
    println("ANSWER $left")

//    println(findOreNeeded(reactions, 1000000L) < 1000000000000L)
}