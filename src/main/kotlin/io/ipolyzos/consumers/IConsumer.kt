package io.ipolyzos.parallel

import io.confluent.parallelconsumer.ParallelConsumerOptions
import io.confluent.parallelconsumer.ParallelStreamProcessor
import io.ipolyzos.config.AppConfig
import mu.KLogger
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

fun main() = IConsumer.runConsumer()

object IConsumer {
    private val logger: KLogger by lazy { KotlinLogging.logger {} }

    fun runConsumer() {
        val properties = AppConfig.getConsumerProps()

        val numConsumers = 3
        val counter = AtomicInteger(0)
        var t0 = System.currentTimeMillis()

        (1 .. numConsumers).forEach { id ->
            thread {
                logger.info { "Starting consumer '$id'" }
                val name = "[Consumer-$id]"
                val consumer: KafkaConsumer<String, String> = KafkaConsumer<String, String>(properties)

                val options: ParallelConsumerOptions<String, String> =
                    ParallelConsumerOptions.builder<String, String>()
                        .ordering(ParallelConsumerOptions.ProcessingOrder.KEY)
                        .maxConcurrency(200)
                        .consumer(consumer)
                        .batchSize(5)
                        .commitMode(ParallelConsumerOptions.CommitMode.PERIODIC_CONSUMER_ASYNCHRONOUS)
                        .build()

                val eos = ParallelStreamProcessor.createEosStreamProcessor(options)
                eos.subscribe(listOf(AppConfig.MULTIPLE_PARTITIONS_TOPIC))
                val random = Random()
                var initLoop = false

                eos.poll { context ->
                    if (!initLoop) {
                        initLoop = true
                        t0 = System.currentTimeMillis()
                    }

                    val processingTime = measureTimeMillis {
                        context.consumerRecordsFlattened.forEach { _ ->
                            Thread.sleep(100)
                        }
                    }

                    counter.addAndGet(context.size().toInt())
                    logger.info { "$name: Processing '${context.size()}' records in $processingTime millis. Total: ${counter.get()}" }
                    logger.info { "$name: Elapsed Time so far: ${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - t0)} seconds." }
                }
            }
        }
    }
}
