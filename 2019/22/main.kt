package twentytwo

import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import kotlin.math.absoluteValue

typealias Deck = List<Int>


interface Dealer {
    fun deal(deck: Deck): Deck
}

class Cut(private val n: Int): Dealer {
    override fun deal(deck: Deck): Deck {
        if (n > 0) {
            val firstN = deck.take(n)
            return deck.drop(n).plus(firstN)
        } else {
            val lastN = deck.takeLast(n.absoluteValue)
            return lastN.plus(deck.dropLast(n.absoluteValue))
        }
    }
}

class DealWithIncrement(private val n: Int): Dealer {
    override fun deal(deck: Deck): Deck {
        val size = deck.size
        val newDeck = ArrayList<Int>(deck)
        
        var idx = 0
        deck.forEach { 
            newDeck[idx] = it
            idx = (idx + n) % size
        }
        
        return newDeck
    }
}

class DealIntoNewStack: Dealer {
    override fun deal(deck: Deck): Deck {
        return deck.reversed()
    }
}

fun parseInput(input: String): List<Dealer> {
    val cutRe = Regex("cut (-?\\d+)")
    val dealWithIncrementRe = Regex("deal with increment (-?\\d+)")
    
    return input.lines().map{
        when {
            cutRe.matches(it) -> {
                val n = cutRe.matchEntire(it)!!.groupValues[1].toInt()
                Cut(n)
            }
            dealWithIncrementRe.matches(it) -> {
                val n = dealWithIncrementRe.matchEntire(it)!!.groupValues[1].toInt()
                DealWithIncrement(n)
            }
            it == "deal into new stack" -> {
                DealIntoNewStack()
            }
            else -> {
                throw IllegalArgumentException("Unrecognized $it")
            }
        }
    }
}

@Test fun Test1() {
    val input = """
        deal with increment 7
        deal into new stack
        deal into new stack
    """.trimIndent()
    println(input)
}

fun main() {
    val dealingSeq = parseInput(INPUT)
    val deck = (0 until 12345).toList()
    
    val deckAfter = dealingSeq.fold(deck) { acc, dealer -> dealer.deal(acc) }
    
    println(deckAfter)
    println(deckAfter.indexOf(2019))
}