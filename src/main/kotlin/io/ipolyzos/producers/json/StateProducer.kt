package io.ipolyzos.producers.json

import io.ipolyzos.config.KafkaConfig
import io.ipolyzos.models.clickstream.ClickEvent
import io.ipolyzos.show
import io.ipolyzos.utils.DataSourceUtils
import mu.KLogger
import mu.KotlinLogging
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun main() = ECommerceProducer.runProducer()

object ECommerceProducer {
    private val logger: KLogger by lazy { KotlinLogging.logger {} }

    fun runProducer() {
        val events: Sequence<ClickEvent> = DataSourceUtils
            .loadDataFile("/Documents/data/clickevents/small/events.csv", DataSourceUtils.toEvent)

        val properties = KafkaConfig.buildProducerProps()

        logger.info("Starting Kafka Producers with configs ...")
        properties.show()

        val producer = KafkaProducer<String, ClickEvent>(properties)

        val counter = AtomicInteger(0)
        val t0 = System.currentTimeMillis()

        for (event in events) {
            ProducerRecord(KafkaConfig.EVENTS_TOPIC, event.userSession, event)
                .also { record ->
                    producer.send(record) { _, exception ->
                        exception?.let {
                            logger.error { "Error while producing: $exception" }
                        } ?: kotlin.run {
                            counter.incrementAndGet()
                            if (counter.get() % 10000 == 0) {
                                logger.info { "Total messages sent so far ${counter.get()}." }
                            }
                        }
                    }
                }
        }

        producer.flush()
        logger.info("Total Event records sent: '${counter.get()}' in '${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - t0)}' seconds")

        logger.info("Closing Producers ...");

        producer.close()
    }
}