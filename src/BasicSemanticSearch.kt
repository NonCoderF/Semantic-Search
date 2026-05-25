/*
The Algorithm
Step 1
Convert every word → vector

Step 2
Average vectors for a document

Step 3
Average vectors for query

Step 4
Compare similarity using cosine similarity
 */

import kotlin.math.sqrt

data class Document(
    val text: String
)

data class Vector(
    val values: DoubleArray
)

fun main() {

    val documents = listOf(
        Document("android kotlin compose"),
        Document("stock market trading"),
        Document("mvi clean architecture"),
        Document("mobile ui framework")
    )

    val query = "android ui toolkit"

    val results = semanticSearch(query, documents)

    println("\nRESULTS:\n")

    results.forEach {
        println("${it.first.text} -> similarity=${"%.3f".format(it.second)}")
    }
}

/**
 * WORD EMBEDDINGS
 *
 * 5 dimensions:
 *
 * [tech, mobile, finance, architecture, programming]
 */
val wordVectors = mapOf(

    "android" to vectorOf(0.9, 0.9, 0.1, 0.3, 0.8),

    "kotlin" to vectorOf(0.8, 0.7, 0.1, 0.4, 0.9),

    "compose" to vectorOf(0.7, 0.9, 0.0, 0.5, 0.7),

    "mobile" to vectorOf(0.7, 1.0, 0.0, 0.2, 0.5),

    "ui" to vectorOf(0.6, 0.9, 0.0, 0.4, 0.4),

    "framework" to vectorOf(0.8, 0.5, 0.0, 0.7, 0.8),

    "toolkit" to vectorOf(0.7, 0.5, 0.0, 0.5, 0.6),

    "stock" to vectorOf(0.1, 0.0, 1.0, 0.1, 0.1),

    "market" to vectorOf(0.1, 0.0, 0.9, 0.2, 0.1),

    "trading" to vectorOf(0.0, 0.0, 1.0, 0.1, 0.2),

    "mvi" to vectorOf(0.7, 0.3, 0.0, 1.0, 0.8),

    "clean" to vectorOf(0.5, 0.2, 0.0, 0.9, 0.6),

    "architecture" to vectorOf(0.6, 0.2, 0.0, 1.0, 0.5)
)

fun semanticSearch(
    query: String,
    documents: List<Document>
): List<Pair<Document, Double>> {

    val queryVector = sentenceToVector(query)

    return documents.map { document ->

        val docVector = sentenceToVector(document.text)

        val similarity = cosineSimilarity(
            queryVector.values,
            docVector.values
        )

        document to similarity

    }.sortedByDescending { it.second }
}

fun sentenceToVector(sentence: String): Vector {

    val words = normalize(sentence)

    val vectorSize = 5

    val sum = DoubleArray(vectorSize)

    var count = 0

    for (word in words) {

        val vector = wordVectors[word]

        if (vector != null) {

            for (i in vector.values.indices) {
                sum[i] += vector.values[i]
            }

            count++
        }
    }

    if (count == 0) {
        return Vector(DoubleArray(vectorSize))
    }

    for (i in sum.indices) {
        sum[i] /= count
    }

    return Vector(sum)
}

fun cosineSimilarity(a: DoubleArray, b: DoubleArray): Double {

    var dot = 0.0
    var magA = 0.0
    var magB = 0.0

    for (i in a.indices) {

        dot += a[i] * b[i]

        magA += a[i] * a[i]

        magB += b[i] * b[i]
    }

    return dot / (sqrt(magA) * sqrt(magB))
}

fun normalize(text: String): List<String> {

    return text
        .lowercase()
        .split(" ")
        .map { it.trim() }
        .filter { it.isNotBlank() }
}

fun vectorOf(
    a: Double,
    b: Double,
    c: Double,
    d: Double,
    e: Double
): Vector {

    return Vector(doubleArrayOf(a, b, c, d, e))
}