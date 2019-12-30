package two

enum class Operator(val value: Int) {
    ADD(1),
    MUL(2),
    HALT(99)
}

fun runProgram(input: Array<Int>): Array<Int> {
    val result = input.copyOf()
    var i = 0

    while (true) {
        val op = result[i * 4]
        if (Operator.HALT.value == op) {
            break
        }

        val argIdx1 = result[i * 4 + 1]
        val argIdx2 = result[i * 4 + 2]
        val resIdx = result[i * 4 + 3]

        if (Operator.ADD.value == op) {
            result[resIdx] = result[argIdx1] + result[argIdx2]
        }

        if (Operator.MUL.value == op) {
            result[resIdx] = result[argIdx1] * result[argIdx2]
        }

        i++
    }

    return result

}

fun first(input: Array<Int>) {
    input[1] = 12
    input[2] = 2

    val res = runProgram(input)

    println("First result ${res[0]}")
}

fun second(input: Array<Int>) {
    val DESIRED = 19690720

    (0..100).forEach{ noun ->
        val inputCopy = input.copyOf()
        val verbFound = (0..100).find { verb ->
            inputCopy[1] = noun
            inputCopy[2] = verb
            val res = runProgram(inputCopy)
            res[0] == DESIRED
        }

        if (verbFound != null) {
            println("NOUN:  $noun VERB: $verbFound")
            println("Second result: ${100 * noun + verbFound}")
        }

    }



}

fun main() {
    first(INPUT)
    second(INPUT)
}