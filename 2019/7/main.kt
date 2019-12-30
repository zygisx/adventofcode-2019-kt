package seven

import java.lang.IllegalArgumentException


enum class Operator(val value: Int) {
    ADD(1),
    MUL(2),
    IN(3),
    OUT(4),
    JMPT(5),
    JMPF(6),
    LT(7),
    ET(8),
    HALT(99);

    companion object {
        fun fromInt(value: Int): Operator {
            return when(value) {
                ADD.value -> ADD
                MUL.value -> MUL
                IN.value -> IN
                OUT.value -> OUT
                JMPT.value -> JMPT
                JMPF.value -> JMPF
                LT.value -> LT
                ET.value -> ET
                HALT.value -> HALT
                else -> throw IllegalArgumentException("Unrecognized ParameterMode $value")
            }
        }
    }
}

enum class ParameterMode(val value: Int) {
    POSITION(0),
    IMMEDIATE(1);

    companion object {
        fun fromInt(value: Int): ParameterMode {
            return when(value) {
                0 -> POSITION
                1 -> IMMEDIATE
                else -> throw IllegalArgumentException("Unrecognized ParameterMode")
            }
        }
    }
}

// OPCODE:
// DCBAA
// AA - Opearator
// B - 1st param mode
// C - 2nd param mode
// D - 3rd param mode

fun getParameterMode(operator: Int, paramIndex: Int): ParameterMode {
    val multiplier = Math.pow(10.0, paramIndex.toDouble()).toInt() * 10
    val param = operator % (multiplier * 10) / multiplier
    return ParameterMode.fromInt(param)
}

fun getOpCode(operator: Int): Operator {
    val opCode = operator % 100
    return Operator.fromInt(opCode)
}

fun getArgument(parameterMode: ParameterMode, arg: Int, input: Array<Int>): Int {
    return when (parameterMode) {
        ParameterMode.IMMEDIATE -> arg
        ParameterMode.POSITION -> input[arg]
    }
}

data class Program(val program: Array<Int>, val position: Int, val dead: Boolean, val output: Int?)

fun runProgram(p: Program, input: MutableList<Int>): Program {
    val result = p.program.copyOf()
    var i = p.position
    var out: Int? = null

    while (true) {
        val instruction = result[i]

        val op = getOpCode(instruction)

        if (Operator.HALT == op) {
            return Program(result, i++, true, out)
        }

        val biOp = fun(doOp: (Int, Int) -> Int) {
            i++
            val arg1 = getArgument(getParameterMode(instruction, 1), result[i], result)
            i++
            val arg2 = getArgument(getParameterMode(instruction, 2), result[i], result)
            i++
            val resIdx = result[i]
            result[resIdx] = doOp(arg1,arg2)
        }

        if (Operator.ADD == op) {
            biOp{ arg1, arg2 -> arg1 + arg2}
        }

        if (Operator.MUL == op) {
            biOp{ arg1, arg2 -> arg1 * arg2 }
        }

        if (Operator.IN == op) {
            i++
            val arg1 = result[i]
//            println("Taking input ${input[0]}")
            result[arg1] = input.removeAt(0)
        }

        if (Operator.OUT == op) {
            i++
            val arg1 = getArgument(getParameterMode(instruction, 1), result[i], result)
            out = arg1
//            println("OUTPUT ADD: ${arg1}")
            i++
            return Program(result, i, false, out)
        }

        val jumpOp = fun(predicate: (arg1: Int) -> Boolean) {
            i++
            val arg1 = getArgument(getParameterMode(instruction, 1), result[i], result)
            if (predicate(arg1)) {
                i++
                val arg2 = getArgument(getParameterMode(instruction, 2), result[i], result)
                i = arg2 - 1
            } else {
                i++
            }
        }

        if (Operator.JMPT == op) {
            jumpOp{ it != 0 }
        }

        if (Operator.JMPF == op) {
            jumpOp{ it == 0}
        }

        val compareOp = fun(predicate: (Int, Int) -> Boolean) {
            i++
            val arg1 = getArgument(getParameterMode(instruction, 1), result[i], result)
            i++
            val arg2 = getArgument(getParameterMode(instruction, 2), result[i], result)
            i++
            val resIdx = result[i]
            result[resIdx] =  if (predicate(arg1, arg2)) 1 else 0
        }

        if (Operator.LT == op) {
            compareOp { arg1, arg2 -> arg1 < arg2 }
        }

        if (Operator.ET == op) {
            compareOp { arg1, arg2 -> arg1 == arg2 }
        }

        i++
    }

//    return Program(result, i, true, output)
    throw IllegalArgumentException("WTF")
}


fun runOnPhase(input: Array<Int>, phase: List<Int>): Int {

    val programs = (0..4).map { Program(input, 0, false, null) }.toTypedArray()

    val inputs = (0..4).map { mutableListOf(phase[it]) }
    inputs[0].add(0)

    var out = 0

    fun next(i:  Int) = if (i >= 4) 0 else i + 1


    var stop = false
    while (!stop) {

        for (i in 0..4) {
            programs[i] = runProgram(programs[i], inputs[i])
            if (programs[i].output == null) {
                stop = true
                break
            }
            out = programs[i].output!!
            inputs[next(i)].add(programs[i].output!!)
//            println("$i D: ${programs[i].dead}   OUT: ${programs[i].output} P: ${programs[i].position}")
        }
    }

    return out

//    Input = mutableListOf(phase[1], Output[0])
//    Output.clear()
//    val p2 = Program(input, 0, false)
//    result = runProgram(p2)
//
//    Input = mutableListOf(phase[2], Output[0])
//    Output.clear()
//    val p3 = Program(input, 0, false)
//    result = runProgram(p3)
//
//    Input = mutableListOf(phase[3], Output[0])
//    Output.clear()
//    val p4 = Program(input, 0, false)
//    result = runProgram(p4)
//
//    Input = mutableListOf(phase[4], Output[0])
//    Output.clear()
//    val p5 = Program(input, 0, false)
//    result = runProgram(p5)

//    return Output[0]


}

//var Input = mutableListOf(0)
//var Output = mutableListOf(0)

fun permute(input: List<Int>): List<List<Int>> {
    if (input.size == 1) return listOf(input)
    val perms = mutableListOf<List<Int>>()
    val toInsert = input[0]
    for (perm in permute(input.drop(1))) {
        for (i in 0..perm.size) {
            val newPerm = perm.toMutableList()
            newPerm.add(i, toInsert)
            perms.add(newPerm)
        }
    }
    return perms
}


fun main() {
//    println(getParameterMode(1002, 1))
//    println(getParameterMode(1002, 2))
//    println(getParameterMode(1002, 3))

//    println(getParameterMode(1002, 1))
//    println(getParameterMode(1002, 2))
//    println(getParameterMode(1002, 3))

    val permutations = permute(listOf(5, 6, 7, 8, 9))

    val r = runOnPhase(INPUT, listOf(9,8,7,6,5))
    println(r)


    val max = permutations.map {
        val output = runOnPhase(INPUT, it)
        println("OUTPUT: $output")
        output
    }.max()
//
    println(max)
}