package io.ipolyzos.resources


import io.confluent.parallelconsumer.ParallelConsumerOptions
import io.confluent.parallelconsumer.ParallelStreamProcessor
import io.ipolyzos.show
import io.ipolyzos.utils.LoggingUtils
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer

import org.apache.kafka.common.errors.WakeupException
import java.time.Duration
import java.util.Properties
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

context(LoggingUtils)
class ConsumerResource<K, V> private constructor(private val consumer: KafkaConsumer<K, V>) {
    private val counter = AtomicInteger(0)

    companion object {
        fun <K, V> live(properties: Properties): ConsumerResource<K, V> {
            with(LoggingUtils()) {
                logger.info("Starting Kafka Consumer with configs ...")
                properties.show()
                val consumer = KafkaConsumer<K, V>(properties)
                val mainThread = Thread.currentThread()

                Runtime.getRuntime().addShutdownHook(Thread {
                    logger.info("Detected a shutdown, let's exit by calling consumer.wakeup()...")
                    consumer.wakeup()

                    // join the main thread to allow the execution of the code in the main thread
                    try {
                        mainThread.join()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                })
                return ConsumerResource(consumer)
            }
        }
    }

    fun consume(topic: String) {
        consumer.subscribe(listOf(topic))
        val t0 = System.currentTimeMillis()
        try {
            while (true) {
                val records: ConsumerRecords<K, V> = consumer.poll(Duration.ofMillis(100))

                records.forEach { _ ->
                    // simulate the consumers doing some work
//                Thread.sleep(200)
                    records.show()
                    logger.info { "Total so far: ${counter.incrementAndGet()}" }
                    logger.info { "Records in batch: ${records.count()}" }
                    logger.info { "Elapsed Time so far: ${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - t0)} seconds." }

                }
            }
        } catch (e: WakeupException) {
            logger.info("Received Wake up exception!")
        } catch (e: Exception) {
            logger.warn("Unexpected exception: {}", e.message)
        } finally {
//        logger.info { "Committing Current Offsets: ${listener.getCurrentOffsets()}" }
//        consumer.commitSync(listener.getCurrentOffsets()); // we must commit the offsets synchronously here
//    consumer.commitAsync { offsets, exception ->
//        exception?.let {
//            logger.error { "Error while producing: $exception" }
//        } ?: run {
//            logger.info {
//                "Successfully Committed Offsets: $offsets."
//            }
//        }
//    }
            consumer.close() // this will also commit the offsets if need be.
            logger.info("The consumer is now gracefully closed.")
        }
    }

    fun consumeParallel(topic: String) {
        val options: ParallelConsumerOptions<K, V> =
            ParallelConsumerOptions.builder<K, V>()
                .ordering(ParallelConsumerOptions.ProcessingOrder.KEY)
                .maxConcurrency(100)
                .consumer(consumer)
                .batchSize(1000)
                .commitMode(ParallelConsumerOptions.CommitMode.PERIODIC_CONSUMER_ASYNCHRONOUS)
                .build()

        val eos = ParallelStreamProcessor.createEosStreamProcessor(options)
        eos.subscribe(listOf(topic))
        var initLoop = false
        var t0 = System.currentTimeMillis()

        eos.poll { context ->
            if (!initLoop) {
                initLoop = true
                t0 = System.currentTimeMillis()
            }

            val processingTime = measureTimeMillis {
                context.consumerRecordsFlattened.forEach { _ ->
//                    Thread.sleep(100)
                    counter.incrementAndGet()
                }
            }

            logger.info { "Processing '${context.size()}' records in $processingTime millis. Total: ${counter.get()}" }
            logger.info { ": Elapsed Time so far: ${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - t0)} seconds." }
        }
    }
}