package eight

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
    BASEO(9),
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
                BASEO.value -> ET
                HALT.value -> HALT
                else -> throw IllegalArgumentException("Unrecognized ParameterMode $value")
            }
        }
    }
}

enum class ParameterMode(val value: Int) {
    POSITION(0),
    IMMEDIATE(1),
    RELATIVE(2);

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

fun getArgument(parameterMode: ParameterMode, relativeBase: Int, arg: Int, input: HashMap<Int, Int>): Int {
    return when (parameterMode) {
        ParameterMode.IMMEDIATE -> arg
        ParameterMode.POSITION -> input[arg]!!
        ParameterMode.RELATIVE -> input[relativeBase + arg]!!
    }
}

data class Program(val program: HashMap<Int, Int>, val relativeBase: Int)

fun runProgram(program: Program): Program {
    val result = program.program
    var relativeBase = program.relativeBase
    var i = 0

    while (true) {
        val instruction = result[i]!!

        val op = getOpCode(instruction)

        if (Operator.HALT == op) {
            break
        }

        val biOp = fun(doOp: (Int, Int) -> Int) {
            i++
            val arg1 = getArgument(getParameterMode(instruction, 1), relativeBase, result[i]!!, result)
            i++
            val arg2 = getArgument(getParameterMode(instruction, 2), relativeBase, result[i]!!, result)
            i++
            val resIdx = result[i]!!
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
            val arg1 = result[i]!!
            println("Taking input")
            result[arg1] = Input.removeAt(0)
            println(result[arg1])
        }

        if (Operator.OUT == op) {
            i++
            val arg1 = getArgument(getParameterMode(instruction, 1), relativeBase, result[i]!!, result)
            println(arg1)
        }

        val jumpOp = fun(predicate: (arg1: Int) -> Boolean) {
            i++
            val arg1 = getArgument(getParameterMode(instruction, 1), relativeBase, result[i]!!, result)
            if (predicate(arg1)) {
                i++
                val arg2 = getArgument(getParameterMode(instruction, 2), relativeBase, result[i]!!, result)
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
            val arg1 = getArgument(getParameterMode(instruction, 1), relativeBase, result[i]!!, result)
            i++
            val arg2 = getArgument(getParameterMode(instruction, 2), relativeBase, result[i]!!, result)
            i++
            val resIdx = result[i]!!
            result[resIdx] =  if (predicate(arg1, arg2)) 1 else 0
        }

        if (Operator.LT == op) {
            compareOp { arg1, arg2 -> arg1 < arg2 }
        }

        if (Operator.ET == op) {
            compareOp { arg1, arg2 -> arg1 == arg2 }
        }

        if (Operator.BASEO == op) {
            i++
            val arg1 = getArgument(getParameterMode(instruction, 1), relativeBase, result[i]!!, result)
            relativeBase += arg1
        }

        i++
    }

    return Program(result, relativeBase)

}

val Input = mutableListOf(5)

fun main() {
//    println(getParameterMode(1002, 1))
//    println(getParameterMode(1002, 2))
//    println(getParameterMode(1002, 3))

//    println(getParameterMode(1002, 1))
//    println(getParameterMode(1002, 2))
//    println(getParameterMode(1002, 3))

//    runProgram(INPUT)
}