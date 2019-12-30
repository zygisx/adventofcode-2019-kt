package five

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

fun runProgram(input: Array<Int>): Array<Int> {
    val result = input.copyOf()
    var i = 0

    while (true) {
        val instruction = result[i]

        val op = getOpCode(instruction)

        if (Operator.HALT == op) {
            break
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
            println("Taking input")
            result[arg1] = Input.removeAt(0)
            println(result[arg1])
        }

        if (Operator.OUT == op) {
            i++
            val arg1 = getArgument(getParameterMode(instruction, 1), result[i], result)
            println(arg1)
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

    return result

}

val Input = mutableListOf(5)

fun main() {
//    println(getParameterMode(1002, 1))
//    println(getParameterMode(1002, 2))
//    println(getParameterMode(1002, 3))

//    println(getParameterMode(1002, 1))
//    println(getParameterMode(1002, 2))
//    println(getParameterMode(1002, 3))

    runProgram(INPUT)
}