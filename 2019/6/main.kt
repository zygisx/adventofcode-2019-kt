package six

data class OrbitPair(val from: String, val to: String)

fun countParents(map: HashMap<String, String>, node: String): Int {
    val parents= map.filter { it.key == node }
    val indirectParents = parents.map { countParents(map, it.value) }.sum()

    return parents.size + indirectParents
}

fun getParents(map: HashMap<String, String>, root: String): ArrayList<String> {
    val parents = ArrayList<String>()

    var node = root
    while (map.containsKey(node)) {
        val parent = map[node]!!
        node = parent
        parents.add(node)
    }

    return parents
}

fun main() {
    val orbits = INPUT
            .split("\n")
            .map {
                val pair = it.split(")")
                OrbitPair(pair[0], pair[1])
            }
    val map = HashMap<String, String>()

    orbits.forEach {
        map.put(it.to, it.from)
    }


    var counter = 0
    map.keys.forEach{

       counter += countParents(map, it)
    }

    println(counter)

    println("---- PART 2 ----")


    val sanParents = getParents(map, "SAN")
    val youParents = getParents(map, "YOU")

    val parentDistances = ArrayList<Int>()
    var sanIndex = 0
    while (sanIndex < sanParents.size) {
        val sanParent = sanParents[sanIndex]
        val youIndex = youParents.indexOfFirst { it == sanParent }
        if (youIndex != -1) {
            parentDistances.add(sanIndex + youIndex)
        }
        sanIndex++
    }

    val min = parentDistances.min()
    println(min)

}