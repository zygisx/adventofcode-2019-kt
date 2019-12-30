package twentytwo

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Twentytwo {
    
    @Test fun Test1Test() {
        val input = """
        deal with increment 7
        deal into new stack
        deal into new stack
    """.trimIndent()

        val deck = (0 until 10).toList()
        val dealingSeq = parseInput(input)

        val deckAfter = dealingSeq.fold(deck) { acc, dealer -> dealer.deal(acc) }

        assertEquals(arrayListOf(0, 3, 6, 9, 2, 5, 8, 1, 4, 7), deckAfter)
    }
}