package twentyone

fun toAsciiString(chars: List<Long>): String {
    return chars.map { it.toChar() }.joinToString("")
}

fun springScriptToIntCode(springScript: String):  List<Long> {
    return springScript
            .lines()
            .map { it.replaceAfter(" #", "").replace(" #", "") }
            .filterNot { it.isBlank() }
            .joinToString("\n", "", "\n")
            .map { it.toLong() }
    
}

fun runCode(intcode: Program, springScript: String) {
    var input =  mutableListOf<Long>()
    val output =  mutableListOf<Long>()
    val intcode = Intcode(intcode, input, 0, 0, output)

    var counter = 0

    while (!intcode.dead) {
        intcode.run()

        val outString = toAsciiString(intcode.output)

        println("Iteration $counter outputed: ${intcode.output.size}")
        println(outString)

        println(intcode.output)

        input = springScriptToIntCode(springScript).toMutableList()

        intcode.output.clear()
        intcode.input.addAll(input)

        counter++
    } 
}

fun first(program: Program) {
    val script = """
NOT D T
NOT T T # 4th is ground
NOT C J # 3rd  is hole
AND T J # 4th ground  and 3rd hole

NOT A T # 1st is hole

OR T J # 1st hole OR (4th ground AND 3rd hole)
WALK
"""
    runCode(program, script)
}

fun second(program: Program) {
    val script = """
NOT A T
NOT B J
OR T J
NOT C T
OR T J # 1st OR 2nd OR 3rd is hole


AND D J # AND 4th is ground

NOT E T
NOT T T # 5th is ground
OR H T # 5th or 8th is ground

AND T J

RUN
"""
    runCode(program, script)
}

fun main() {
    val LIMIT: Long = 100000
    val PROGRAM = Program()
    (0..LIMIT).forEach { PROGRAM[it] = 0L }
    INPUT.forEachIndexed{ index, inp -> PROGRAM[index.toLong()] = inp }

    
    println("===FIRST===")
    first(Program(PROGRAM))
    println("===SECOND===")
    second(Program(PROGRAM))
}

/**
5th is ground
OR
8th is ground
AND
3rd is hole
AND
4th is ground
AND

OR
5th - hole AND  2th hole
6
.................
.................
.........@.......
#####.#.##.##.###

.................
.................
..@..............
#####.#.##..#####

.................
.................
@................
#####.###########

.................
.................
@................
#####...#########

.................
.................
@................
#####.#..########
        
        
*/