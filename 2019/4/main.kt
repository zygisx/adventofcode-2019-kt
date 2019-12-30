package four

val MIN = 125730
val MAX = 579381

val DigitCount = 6

fun isOk(number: Int): Boolean {
    val sortedChars = number.toString(10).toCharArray().sorted()

    val sortedDigits =sortedChars.joinToString("").toInt(10)

    if (sortedDigits != number) return false

    var last = '.'
    return sortedChars.any {
        val consecutive = it == last
        last = it
        consecutive
    }
}

fun isOk2(number: Int): Boolean {
    val sortedChars = number.toString(10).toCharArray().sorted()

    val sortedDigits = sortedChars.joinToString("").toInt(10)

    if (sortedDigits != number) return false

    var last = '.'
    var idx = 0

    while (idx < DigitCount) {
        val digit = sortedChars[idx]
        if (last == digit) {
            var consecutiveCount = 1
            while (idx < DigitCount && sortedChars[idx] == last) {
                consecutiveCount++
                idx++
            }
            if (consecutiveCount == 2) {
                return true
            }
        } else {
            idx++
        }
        last = digit
    }

    return false
}

fun main() {

    val count = (MIN..MAX).filter { isOk(it) }.count()
    println(count)

//    println(isOk2(123444))

    val count2 = (MIN..MAX).filter { isOk2(it) }.count()
    println(count2)

    /*
    1000 false
    11XX 88
    12XX

    999
    */
}