package twentythree

fun toAsciiString(chars: List<Long>): String {
    return chars.map { it.toChar() }.joinToString("")
}

fun main() {
    val LIMIT: Long = 100000
    val PROGRAM = Program()
    (0..LIMIT).forEach { PROGRAM[it] = 0L }
    INPUT.forEachIndexed{ index, inp -> PROGRAM[index.toLong()] = inp }
    
    
    
    println("===FIRST===")
}
