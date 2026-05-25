import kotlin.math.sqrt

fun main() {
    val search = ContextualTicketSearch()

    val tickets = listOf(
        SupportTicket(
            id = "T-1001",
            title = "Android app crashes on checkout",
            body = "mobile android checkout payment crash after tapping pay button"
        ),
        SupportTicket(
            id = "T-1002",
            title = "Login page rejects valid password",
            body = "authentication login password account user cannot access dashboard"
        ),
        SupportTicket(
            id = "T-1003",
            title = "Invoice export missing tax column",
            body = "billing invoice export csv tax amount missing finance report"
        ),
        SupportTicket(
            id = "T-1004",
            title = "Slow product search results",
            body = "catalog product search filter query slow latency backend"
        ),
        SupportTicket(
            id = "T-1005",
            title = "Card payment fails on mobile",
            body = "android mobile card payment checkout error transaction failed"
        )
    )

    val query = "phone payment error"

    val results = search.search(query, tickets)

    println("\nQUERY: $query\n")
    println("MATCHING SUPPORT TICKETS:\n")

    results.forEach { result ->
        println("${result.ticket.id} | ${result.ticket.title} | score=${"%.3f".format(result.score)}")
    }
}

data class SupportTicket(
    val id: String,
    val title: String,
    val body: String
) {
    val searchableText: String
        get() = "$title $body"
}

data class TicketSearchResult(
    val ticket: SupportTicket,
    val score: Double
)

class ContextualTicketSearch {

    fun search(
        query: String,
        tickets: List<SupportTicket>
    ): List<TicketSearchResult> {
        val documents = tickets.map { it.searchableText }
        val graph = buildContextGraph(documents)
        val vocabulary = graph.keys.toList()
        val wordVectors = createWordVectors(graph, vocabulary)
        val queryVector = sentenceVector(query, wordVectors)

        return tickets
            .map { ticket ->
                val ticketVector = sentenceVector(ticket.searchableText, wordVectors)
                TicketSearchResult(
                    ticket = ticket,
                    score = cosineSimilarity(queryVector, ticketVector)
                )
            }
            .sortedByDescending { it.score }
    }

    private fun buildContextGraph(
        documents: List<String>
    ): MutableMap<String, MutableMap<String, Double>> {
        val graph = mutableMapOf<String, MutableMap<String, Double>>()
        val windowSize = 3

        for (document in documents) {
            val words = normalize(document)

            for (index in words.indices) {
                val current = words[index]
                graph.putIfAbsent(current, mutableMapOf())

                val start = maxOf(0, index - windowSize)
                val end = minOf(words.lastIndex, index + windowSize)

                for (neighborIndex in start..end) {
                    if (index == neighborIndex) continue

                    val neighbor = words[neighborIndex]
                    val distance = kotlin.math.abs(index - neighborIndex)
                    val weight = 1.0 / distance

                    graph[current]!!.merge(neighbor, weight, Double::plus)
                }
            }
        }

        return graph
    }

    private fun createWordVectors(
        graph: Map<String, Map<String, Double>>,
        vocabulary: List<String>
    ): Map<String, DoubleArray> {
        return vocabulary.associateWith { word ->
            val neighbors = graph[word].orEmpty()

            DoubleArray(vocabulary.size) { index ->
                neighbors[vocabulary[index]] ?: 0.0
            }
        }
    }

    private fun sentenceVector(
        sentence: String,
        wordVectors: Map<String, DoubleArray>
    ): DoubleArray {
        if (wordVectors.isEmpty()) return DoubleArray(0)

        val vectorSize = wordVectors.values.first().size
        val sum = DoubleArray(vectorSize)
        var knownWordCount = 0

        for (word in normalize(sentence)) {
            val vector = wordVectors[word] ?: continue

            for (index in vector.indices) {
                sum[index] += vector[index]
            }

            knownWordCount++
        }

        if (knownWordCount == 0) return DoubleArray(vectorSize)

        for (index in sum.indices) {
            sum[index] /= knownWordCount
        }

        return sum
    }

    private fun cosineSimilarity(
        first: DoubleArray,
        second: DoubleArray
    ): Double {
        var dot = 0.0
        var firstMagnitude = 0.0
        var secondMagnitude = 0.0

        for (index in first.indices) {
            dot += first[index] * second[index]
            firstMagnitude += first[index] * first[index]
            secondMagnitude += second[index] * second[index]
        }

        if (firstMagnitude == 0.0 || secondMagnitude == 0.0) return 0.0

        return dot / (sqrt(firstMagnitude) * sqrt(secondMagnitude))
    }

    private fun normalize(text: String): List<String> {
        return text
            .lowercase()
            .replace(Regex("[^a-z ]"), " ")
            .split(" ")
            .filter { it.isNotBlank() }
    }
}
