package io.ipolyzos

import mu.KLogger
import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

private val logger: KLogger by lazy { KotlinLogging.logger {} }

fun <K, V> ProducerRecord<K, V>.show(){
    // TODO:
}

fun Properties.show() {
    val data: MutableList<List<String>> = mutableListOf(listOf("Key", "Value"))
    this.forEach { k, v -> data.add(listOf(k.toString(), v.toString())) }
    formatListAsTable(data)
}

fun formatListAsTable(table: List<List<Any>>) {
    val colWidths = transpose(table)
        .map { it.map { cell -> cell.toString().length }.max() + 2 }

    // Format each row
    val rows: List<String> = table.map {
        it.zip(colWidths).joinToString(
            prefix = "|",
            postfix = "|",
            separator = "|"
        ) { (item, size) -> (" %-" + (size - 1) + "s").format(item) }
    }

    val separator: String = colWidths.joinToString(prefix = "+", postfix = "+", separator = "+") { "-".repeat(it) }
    val data = rows.drop(1).joinToString("\n")

    val tableHeader = "\n$separator\n${rows.first()}\n$separator"
    if (data.isEmpty()) {
        logger.info { tableHeader }
    } else {
        logger.info { "$tableHeader\n$data\n$separator" }
    }
}

fun <E> transpose(xs: List<List<E>>): List<List<E>> {
    fun <E> List<E>.head(): E = this.first()
    fun <E> List<E>.tail(): List<E> = this.takeLast(this.size - 1)
    fun <E> E.append(xs: List<E>): List<E> = listOf(this).plus(xs)

    xs.filter { it.isNotEmpty() }.let { ys ->
        return when (ys.isNotEmpty()) {
            true -> ys.map { it.head() }.append(transpose(ys.map { it.tail() }))
            else -> emptyList()
        }
    }
}