package io.ipolyzos.consumers

import io.ipolyzos.config.KafkaConfig
import io.ipolyzos.models.clickstream.ClickEvent
import io.ipolyzos.show
import mu.KLogger
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private val logger: KLogger by lazy { KotlinLogging.logger {} }

fun main() {
    val properties = KafkaConfig.buildConsumerProps(
        "ecommerce.events.group1"
    )
//    properties[ConsumerConfig.GROUP_ID_CONFIG] = "test.group-0"
//    properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
//    properties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "false"
//    properties[ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG] = 2000
//    properties[ConsumerConfig.FETCH_MIN_BYTES_CONFIG] = 10000
//    properties[ConsumerConfig.FETCH_MAX_BYTES_CONFIG] = 20001

    logger.info { "Starting consumer with properties:" }
    properties.show()
    val consumer: KafkaConsumer<String, ClickEvent> = KafkaConsumer<String, ClickEvent>(properties)
    consumer.subscribe(listOf(KafkaConfig.EVENTS_TOPIC))

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

//    val listener = ConsumerRebalanceListenerImpl(consumer)

    val t0 = System.currentTimeMillis()
    val counter = AtomicInteger(0)
    try {
        while (true) {
            val records: ConsumerRecords<String, ClickEvent> = consumer.poll(Duration.ofSeconds(1))
//            records.forEach { record ->
//                logger.info {
//                    "\t${record.key()}: ${record.value().take(10)} - offsets: ${record.offset()}"
////                    "Received Key: ${it.key()}. Processing time '' millis. Total so far: ${counter.incrementAndGet()}"
//                }
//                listener.addOffsetToTrack(record.topic(), record.partition(), record.offset());
//            }

            if (records.count() > 0) {
                records.show()
                logger.info { "Total so far: ${counter.addAndGet(records.count())}" }
            }
//            if (records.count() > 0) {
//                logger.info { "Records in batch: ${records.count()}" }
//                logger.info { "Elapsed Time so far: ${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - t0)} seconds." }
//
//                consumer.commitAsync { offsets, exception ->
//                    exception?.let {
//                        logger.error { "Error while producing: $exception" }
//                    } ?: run {
//                        logger.info {
//                            "Successfully Committed Offsets: $offsets."
//                        }
//                    }
//                }
//            }
        }
    } catch (e: WakeupException) {
        logger.info("Received Wake up exception!")
    } catch (e: Exception) {
        logger.warn("Unexpected exception: {}", e.message)
    } finally {
//        logger.info { "Committing Current Offsets: ${listener.getCurrentOffsets()}" }
//        consumer.commitSync(listener.getCurrentOffsets()); // we must commit the offsets synchronously here

        consumer.close() // this will also commit the offsets if need be.
        logger.info("The consumer is now gracefully closed.")
    }
}