package eleven

typealias Program = HashMap<Long, Long>

enum class Operator(val value: Long) {
    ADD(1),
    MUL(2),
    IN(3),
    OUT(4),
    JMPT(5),
    JMPF(6),
    LT(7),
    ET(8),
    REBASE(9),
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
                REBASE.value -> REBASE
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

class Intcode(val program: Program, var input: MutableList<Long>, var relative: Long, var idx: Long, var output: MutableList<Long> = mutableListOf()) {
    var dead = false

    private fun getParameterMode(operator: Long, paramIndex: Long): ParameterMode {
        val multiplier = Math.pow(10.0, paramIndex.toDouble()).toInt() * 10
        val param = operator % (multiplier * 10) / multiplier
        return ParameterMode.fromInt(param)
    }

    private fun getOpCode(operator: Long): Operator {
        val opCode = operator % 100
        return Operator.fromInt(opCode)
    }

    private fun getArgument(parameterMode: ParameterMode, arg: Long): Long {
        return when (parameterMode) {
            ParameterMode.POSITION -> this.program[arg]!!
            ParameterMode.IMMEDIATE -> arg
            ParameterMode.RELATIVE -> this.program[this.relative + arg]!!
        }
    }

    private fun getWriteArgument(parameterMode: ParameterMode, arg: Long): Long {
        return when (parameterMode) {
            ParameterMode.POSITION -> arg
            ParameterMode.RELATIVE -> arg + this.relative
            else -> throw IllegalArgumentException("Invalid write parameter mode $parameterMode")
        }
    }

    private fun biOperation(instruction: Long, doOp: (Long, Long) -> Long) {
        idx++
        val arg1 = getArgument(getParameterMode(instruction, 1), this.program[idx]!!)
        idx++
        val arg2 = getArgument(getParameterMode(instruction, 2), this.program[idx]!!)
        idx++
        val resIdx = getWriteArgument(getParameterMode(instruction, 3), this.program[idx]!!)
        this.program[resIdx] = doOp(arg1,arg2)
    }

    private fun jumpOperation(instruction: Long, predicate: (arg1: Long) -> Boolean) {
        idx++
        val arg1 = getArgument(getParameterMode(instruction, 1), this.program[idx]!!)
        if (predicate(arg1)) {
            idx++
            val arg2 = getArgument(getParameterMode(instruction, 2), this.program[idx]!!)
            idx = arg2 - 1
        } else {
            idx++
        }
    }

    private fun compareOperation(instruction: Long, predicate: (Long, Long) -> Boolean) {
        idx++
        val arg1 = getArgument(getParameterMode(instruction, 1), this.program[idx]!!)
        idx++
        val arg2 = getArgument(getParameterMode(instruction, 2), this.program[idx]!!)
        idx++
//      val resIdx = if (getParameterMode(instruction, 3) == ParameterMode.RELATIVE) result[i]!! + relativeBase else result[i]!!
        val resIdx = getWriteArgument(getParameterMode(instruction, 3), this.program[idx]!!)
        this.program[resIdx] =  if (predicate(arg1, arg2)) 1L else 0L
    }

    fun run() {
        while (true) {
            val instruction = this.program[idx]!!
            val op = getOpCode(instruction)

            if (Operator.HALT == op) {
                this.dead = true
                break
            }

            if (Operator.ADD == op) {
                biOperation(instruction) { arg1, arg2 -> arg1 + arg2}
            }

            if (Operator.MUL == op) {
                biOperation(instruction) { arg1, arg2 -> arg1 * arg2 }
            }

            if (Operator.IN == op) {
                idx++
                val arg1 = getWriteArgument(getParameterMode(instruction, 1), this.program[idx]!!)
                this.program[arg1] = this.input.removeAt(0)
                println("Taking input ${this.program[arg1]}")
            }

            if (Operator.OUT == op) {
                idx++
                val arg1 = getArgument(getParameterMode(instruction, 1), this.program[idx]!!)
                output.add(arg1)
                println("OUT: $arg1 ${output.size}")
                if (output.size ==  2) {
                    idx++
                    break
                }
            }

            if (Operator.JMPT == op) {
                jumpOperation(instruction) { it != 0L }
            }

            if (Operator.JMPF == op) {
                jumpOperation(instruction) { it == 0L }
            }

            if (Operator.LT == op) {
                compareOperation(instruction) { arg1, arg2 -> arg1 < arg2 }
            }

            if (Operator.ET == op) {
                compareOperation(instruction) { arg1, arg2 -> arg1 == arg2 }
            }

            if (Operator.REBASE == op) {
                idx++
                val arg1 = getArgument(getParameterMode(instruction, 1), this.program[idx]!!)
                relative += arg1
            }

            idx++
        }
    }
}