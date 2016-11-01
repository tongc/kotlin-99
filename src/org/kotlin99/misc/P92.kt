package org.kotlin99.misc

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import org.kotlin99.common.containsAll
import org.kotlin99.graphs.Graph
import org.kotlin99.graphs.Graph.TermForm
import org.kotlin99.graphs.Graph.TermForm.Term
import org.kotlin99.graphs.toGraph
import org.kotlin99.graphs.toTermForm
import java.util.*

fun <T> Graph<T, *>.gracefulLabeling(): Sequence<Graph<String, Nothing>> {
    val edgeLabels = 1.rangeTo(edges.size).toHashSet()
    return 1.rangeTo(nodes.size).toList().combinationsSeq()
        .map{ nodes.keys.zip(it).toMap() }
        .filter{ mapping ->
            val diffs = edges.mapTo(HashSet()) { edge ->
                Math.abs(mapping[edge.n1.value]!! - mapping[edge.n2.value]!!)
            }
            diffs == edgeLabels
        }
        .map { mapping ->
            toTermForm().run {
                Graph.terms(TermForm(
                    nodes.map { mapping[it]!!.toString() },
                    edges.map{ Term<String, Nothing>(mapping[it.n1]!!.toString(), mapping[it.n2]!!.toString()) }
                ))
            }
        }
}

fun <T> List<T>.combinationsSeq(): Sequence<List<T>> {
    if (size <= 1) return sequenceOf(this)
    val head = first()
    return drop(1).combinationsSeq().flatMap{ subCombination ->
        (0..subCombination.size).asSequence().map { i ->
            LinkedList(subCombination).apply{ add(i, head) }
        }
    }
}

class P92Test {
    @Test fun `basic graceful labeling`() {
        assertThat("[a]".toGraph().gracefulLabeling().first(), equalTo("[1]".toGraph()))
        assertThat("[a-b]".toGraph().gracefulLabeling().first(), equalTo("[2-1]".toGraph()))
        assertThat("[a-b, a-c]".toGraph().gracefulLabeling().first(), equalTo("[3-1, 3-2]".toGraph()))
    }

    @Test fun `graceful labeling of examples in readme`() {
        assertThat("[a-d, a-g, a-b, b-c, b-e, e-f]".toGraph().gracefulLabeling().first(), equalTo(
                "[7-2, 7-1, 7-3, 3-6, 3-5, 5-4]".toGraph()
        ))

        // TODO too slow
//        assertThat("[a-i, a-h, a-g, a-b, a-c, c-f, c-d, d-k, c-e, e-g, g-m, g-n, n-p]".toGraph().gracefulLabeling().first(), equalTo(
//                "[7-2, 7-1, 7-3, 3-6, 3-5, 5-4]".toGraph()
//        ))
    }

    @Test fun `combinations sequence`() {
        assertThat(listOf(1, 2, 3).combinationsSeq().toList(), containsAll(listOf(
                listOf(1, 2, 3),
                listOf(1, 3, 2),
                listOf(2, 1, 3),
                listOf(2, 3, 1),
                listOf(3, 1, 2),
                listOf(3, 2, 1)
        )))
    }
}