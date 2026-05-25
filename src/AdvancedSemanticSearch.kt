/*
Core Idea
Step 1
Collect documents.

Step 2
For every word:

Look at nearby words.
Step 3

Increase relationship score.

Example

Document:
android kotlin compose

Window size = 1
Relationships:
android <-> kotlin
kotlin <-> compose

Weight increases.

Semantic Graph

Eventually:

android:
   kotlin -> 8
   compose -> 6
   mobile -> 9

stock:
   market -> 10
   trading -> 7

Now we are LEARNING meaning.

Final Basic Algorithm

Documents
   ↓
Learn word relationships automatically
   ↓
Generate vectors dynamically
   ↓
Search semantically
 */

import kotlin.math.sqrt

fun main() {
    val search = AdvancedSemanticSearch()
    search.run()
}

class AdvancedSemanticSearch {

    fun run() {

        val documents = listOf(

            "android kotlin compose ui mobile",

            "android framework kotlin development",

            "stock market trading finance",

            "clean architecture mvi android",

            "mobile ui compose framework"
        )

        // STEP 1
        val graph = buildSemanticGraph(documents)

        println("\nSEMANTIC GRAPH:\n")

        graph.forEach { (word, neighbors) ->

            println("$word -> ${neighbors.keys}")
        }

        // STEP 2
        val vocabulary = graph.keys.toList()

        // STEP 3
        val vectors = generateWordVectors(graph, vocabulary)

        println("\nWORD VECTORS:\n")

        vectors.forEach { (word, vector) ->

            println("$word -> ${vector.joinToString(prefix = "[", postfix = "]")}")
        }

        // STEP 4
        val query = "android ui toolkit"

        println("\nQUERY: $query\n")

        val results = semanticSearch(
            query = query,
            documents = documents,
            wordVectors = vectors
        )

        println("RESULTS:\n")

        results.forEach {

            println("${it.first} -> similarity=${"%.3f".format(it.second)}")
        }
    }

    fun buildSemanticGraph(
        documents: List<String>
    ): MutableMap<String, MutableMap<String, Double>> {

        val graph = mutableMapOf<String, MutableMap<String, Double>>()

        val windowSize = 2

        for (doc in documents) {

            val words = normalize(doc)

            for (i in words.indices) {

                val current = words[i]

                graph.putIfAbsent(current, mutableMapOf())

                val start = maxOf(0, i - windowSize)
                val end = minOf(words.size - 1, i + windowSize)

                for (j in start..end) {

                    if (i == j) continue

                    val neighbor = words[j]

                    val distance = kotlin.math.abs(i - j)

                    // Closer words get higher weight
                    val weight = 1.0 / distance

                    graph[current]!!
                        .merge(neighbor, weight, Double::plus)
                }
            }
        }

        return graph
    }

    /**
     * Create dynamic vectors from graph
     *
     * Each dimension = relationship weight
     * with another vocabulary word.
     */
    fun generateWordVectors(
        graph: Map<String, Map<String, Double>>,
        vocabulary: List<String>
    ): Map<String, DoubleArray> {

        val vectors = mutableMapOf<String, DoubleArray>()

        for (word in vocabulary) {

            val vector = DoubleArray(vocabulary.size)

            val neighbors = graph[word] ?: emptyMap()

            for ((index, vocabWord) in vocabulary.withIndex()) {

                vector[index] = neighbors[vocabWord] ?: 0.0
            }

            vectors[word] = vector
        }

        return vectors
    }

    fun semanticSearch(
        query: String,
        documents: List<String>,
        wordVectors: Map<String, DoubleArray>
    ): List<Pair<String, Double>> {

        val queryVector = sentenceVector(query, wordVectors)

        return documents.map { doc ->

            val docVector = sentenceVector(doc, wordVectors)

            val similarity = cosineSimilarity(
                queryVector,
                docVector
            )

            doc to similarity

        }.sortedByDescending { it.second }
    }

    fun sentenceVector(
        sentence: String,
        wordVectors: Map<String, DoubleArray>
    ): DoubleArray {

        val words = normalize(sentence)

        if (wordVectors.isEmpty()) {
            return DoubleArray(0)
        }

        val vectorSize = wordVectors.values.first().size

        val sum = DoubleArray(vectorSize)

        var count = 0

        for (word in words) {

            val vector = wordVectors[word] ?: continue

            for (i in vector.indices) {
                sum[i] += vector[i]
            }

            count++
        }

        if (count == 0) {
            return DoubleArray(vectorSize)
        }

        for (i in sum.indices) {
            sum[i] /= count
        }

        return sum
    }

    fun cosineSimilarity(
        a: DoubleArray,
        b: DoubleArray
    ): Double {

        var dot = 0.0
        var magA = 0.0
        var magB = 0.0

        for (i in a.indices) {

            dot += a[i] * b[i]

            magA += a[i] * a[i]

            magB += b[i] * b[i]
        }

        if (magA == 0.0 || magB == 0.0) {
            return 0.0
        }

        return dot / (sqrt(magA) * sqrt(magB))
    }

    fun normalize(text: String): List<String> {

        return text
            .lowercase()
            .replace(Regex("[^a-zA-Z ]"), "")
            .split(" ")
            .filter { it.isNotBlank() }
    }
}