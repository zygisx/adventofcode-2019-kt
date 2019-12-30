package nine

import adventofcode.Intcode
import java.lang.IllegalArgumentException


enum class Operator(val value: Long) {
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
        fun fromInt(value: Long): Operator {
            return when(value) {
                ADD.value -> ADD
                MUL.value -> MUL
                IN.value -> IN
                OUT.value -> OUT
                JMPT.value -> JMPT
                JMPF.value -> JMPF
                LT.value -> LT
                ET.value -> ET
                BASEO.value -> BASEO
                HALT.value -> HALT
                else -> throw IllegalArgumentException("Unrecognized ParameterMode $value")
            }
        }
    }
}

enum class ParameterMode(val value: Long) {
    POSITION(0),
    IMMEDIATE(1),
    RELATIVE(2);

    companion object {
        fun fromInt(value: Long): ParameterMode {
            return when(value) {
                POSITION.value -> POSITION
                IMMEDIATE.value -> IMMEDIATE
                RELATIVE.value -> RELATIVE
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

fun getParameterMode(operator: Long, paramIndex: Long): ParameterMode {
    val multiplier = Math.pow(10.0, paramIndex.toDouble()).toInt() * 10
    val param = operator % (multiplier * 10) / multiplier
    return ParameterMode.fromInt(param)
}

fun getOpCode(operator: Long): Operator {
    val opCode = operator % 100
    return Operator.fromInt(opCode)
}

fun getArgument(parameterMode: ParameterMode, relativeBase: Long, arg: Long, input: HashMap<Long,Long>): Long {
    return when (parameterMode) {
        ParameterMode.POSITION -> input[arg]!!
        ParameterMode.IMMEDIATE -> arg
        ParameterMode.RELATIVE -> input[relativeBase + arg]!!
    }
}

data class Program(val program: HashMap<Long,Long>, val relativeBase: Long)

fun runProgram(program: Program): HashMap<Long,Long> {
    val result = program.program //.copyOf()
    var relativeBase = program.relativeBase
    var i = 0L

    while (true) {
        val instruction = result[i]!!

        val op = getOpCode(instruction)
//        println(op)

        if (Operator.HALT == op) {
            break
        }

        val biOp = fun(doOp: (Long, Long) -> Long) {
            i++
            val arg1 = getArgument(getParameterMode(instruction, 1), relativeBase, result[i]!!, result)
            i++
            val arg2 = getArgument(getParameterMode(instruction, 2), relativeBase, result[i]!!, result)
            i++
            val resIdx = if (getParameterMode(instruction, 3) == ParameterMode.RELATIVE) result[i]!! + relativeBase else result[i]!!
//            val resIdx = result[i]!!
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
            val arg1 = if (getParameterMode(instruction, 1) == ParameterMode.RELATIVE) result[i]!!  + relativeBase else result[i]!!
//            val arg1 = result[i]!!
            println("Taking input")
            result[arg1] = Input.removeAt(0)
            println(result[arg1])
        }

        if (Operator.OUT == op) {
            i++
            val arg1 = getArgument(getParameterMode(instruction, 1), relativeBase, result[i]!!, result)
            println("OUT: $arg1")
        }

        val jumpOp = fun(predicate: (arg1: Long) -> Boolean) {
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
            jumpOp{ it != 0L }
        }

        if (Operator.JMPF == op) {
            jumpOp{ it == 0L }
        }

        val compareOp = fun(predicate: (Long, Long) -> Boolean) {
            i++
            val arg1 = getArgument(getParameterMode(instruction, 1), relativeBase, result[i]!!, result)
            i++
            val arg2 = getArgument(getParameterMode(instruction, 2), relativeBase, result[i]!!, result)
            i++
            val resIdx = if (getParameterMode(instruction, 3) == ParameterMode.RELATIVE) result[i]!! + relativeBase else result[i]!!
//            val resIdx = result[i]!!
            result[resIdx] =  if (predicate(arg1, arg2)) 1L else 0L
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
            relativeBase = relativeBase + arg1
//            println("REL $relativeBase $arg1")
        }

        i++
    }

    return result

}

val Input = mutableListOf<Long>(1)

fun main() {
//    println(getParameterMode(1002, 1))
//    println(getParameterMode(1002, 2))
//    println(getParameterMode(1002, 3))

//    println(getParameterMode(1002, 1))
//    println(getParameterMode(1002, 2))
//    println(getParameterMode(1002, 3))

    val LIMIT: Long = 100000
//
    val PROGRAM: HashMap<Long, Long> = HashMap()
//
    (0..LIMIT).forEach { PROGRAM[it] = 0L }
//
    INPUT.forEachIndexed{ index, inp -> PROGRAM[index.toLong()] = inp }

//    println(INP)

//    val res = runProgram(Program(INP, 0))
//    println(res)

    val intcode = Intcode(PROGRAM, Input, 0, 0)

    intcode.run()

    println(intcode.output)
}