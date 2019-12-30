package eight


fun first() {
    val digits = INPUT.toCharArray().asIterable().chunked(150)

    val counts = digits.mapIndexed { index, list ->
        Pair(index, list.count { it == '0' })
    }

    val minPair = counts.minBy { it.second }

    val layerOfInterest = digits[minPair!!.first]

    val ones = layerOfInterest.count { it == '1' }
    val twos = layerOfInterest.count { it == '2' }


    println(ones * twos)
}


fun main() {

    val digits = INPUT.toCharArray().asIterable().chunked(150)

    // digits[100][150]
    // 100 layers
    // 150 in each layer

    val pixels = (0 until 150).map{ pixelIdx ->
        (0 until 100).map { layerIdx -> digits[layerIdx][pixelIdx] }
    }

    val result = pixels.map { it.dropWhile { pix -> pix == '2' }.first() }

    val  str = result
            .map { if (it == '1' ) '█' else ' '}
            .chunked(25)
            .map { it.joinToString("") }
            .joinToString("\n")

    println(str)

}