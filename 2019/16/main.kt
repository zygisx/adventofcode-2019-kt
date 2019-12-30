package sixteen

import kotlin.math.absoluteValue


fun makeSequence(times: Int): Sequence<Int> {
    var seqIdx = 0
    val seq = arrayOf(1, 0, -1, 0)
    return sequence {
        while (true) {
            yieldAll((0 until times).map { seq[seqIdx] })
            seqIdx = if (seqIdx == 3) 0 else seqIdx + 1
        }
    }
}

fun fftIteration(inputSignal: List<Int>): List<Int> {
    val len = inputSignal.size
    return (0 until len).map {
        val offset = it
        val dup = it + 1
        val offsetList = (0 until offset).map { 0 }
        val seq = offsetList + makeSequence(dup).take(len - offset)

        val sum = inputSignal.mapIndexed{idx, digit ->
            val mul = seq[idx]
            digit * mul
        }.sum()
        sum.absoluteValue % 10
    }

}

fun main() {

    val chars = INPUT.toCharArray()

    val digits = chars.map { Character.getNumericValue(it)}
    var inputSignal = digits

    val results = mutableListOf<List<Int>>()
    (0 until 100).forEach {
        val output = fftIteration(inputSignal)
        println("$it ${output.joinToString("")}")
        inputSignal = output
        results.add(inputSignal)
    }

    println("FIRST")

    println(inputSignal)


    var inputSignalBig = (0 until 10000).map { digits }.flatten()
    val offset = 5976683
    var inp = inputSignalBig.takeLast(inputSignalBig.size - offset)
    (0 until 100).forEach {
        var prev = 0
        val inpNext = mutableListOf<Int>()

        (inp.size-1 downTo  0).forEach{
            val digit = (inp[it] + prev) % 10
            inpNext.add(digit)
            prev = digit
        }

        inpNext.reverse()
        inp = inpNext
    }

    println("SECOND")
    println(inp.take(8).joinToString(""))
}