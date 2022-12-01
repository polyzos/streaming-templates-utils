package io.ipolyzos.producers

import io.confluent.kafka.serializers.KafkaJsonSerializer
import io.ipolyzos.config.KafkaConfig
import io.ipolyzos.models.clickstream.ClickEvent
import io.ipolyzos.models.clickstream.Product
import io.ipolyzos.models.clickstream.User
import io.ipolyzos.show
import io.ipolyzos.utils.DataSourceUtils
import mu.KLogger
import mu.KotlinLogging
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun main() = ECommerceProducer.runProducer()

object ECommerceProducer {
    private val logger: KLogger by lazy { KotlinLogging.logger {} }

    fun runProducer() {
//        val events: List<ClickEvent> = DataSourceUtils
//            .loadDataFile("/data/clickevents/events.csv", DataSourceUtils.toEvent)
//
//        val products: List<Product> = DataSourceUtils
//            .loadDataFile("/data/clickevents/products.csv", DataSourceUtils.toProduct, withHeader = false)
//
//        val users: List<User> = DataSourceUtils
//            .loadDataFile("/data/clickevents/users.csv", DataSourceUtils.toUser, withHeader = false)

        val events: Sequence<ClickEvent> = DataSourceUtils
            .loadDataFile("/Documents/data/clickevents/events.csv", DataSourceUtils.toEvent)

        val properties = KafkaConfig.buildProducerProps()

        logger.info("Starting Kafka Producers with configs ...")
        properties.show()

        val producer = KafkaProducer<String, ClickEvent>(properties)
//        val productsProducer = KafkaProducer<String, Product>(properties)
//        val usersProducer = KafkaProducer<String, User>(properties)

        val counter = AtomicInteger(0)
        val t0 = System.currentTimeMillis()

//        users.forEach {
//            val record: ProducerRecord<String, User> =
//                ProducerRecord<String, User>(AppConfig.USERS_TOPIC, it.userId, it)
//            usersProducer.send(record)
//            counter.getAndIncrement()
//        }
//
//        usersProducer.flush()
//        logger.info("Total User records sent: '${counter.get()}' in '${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - t0)}' seconds")
//        counter.set(0)
//
//        products.forEach {
//            val record: ProducerRecord<String, Product> =
//                ProducerRecord<String, Product>(AppConfig.PRODUCTS_TOPIC, it.productCode, it)
//
//            productsProducer.send(record)
//            counter.getAndIncrement()
//        }
//        productsProducer.flush()
//        logger.info("Total Product records sent: '${counter.get()}' in '${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - t0)}' seconds")
//        counter.set(0)

        for (event in events) {
            ProducerRecord(KafkaConfig.EVENTS_TOPIC, event.userSession, event)
                .also { record ->
                    producer.send(record) { _, exception ->
                        exception?.let {
                            logger.error { "Error while producing: $exception" }
                        } ?: kotlin.run {
//                            metadata.show()
                        }
                    }
                }
            counter.getAndIncrement()
            if (counter.incrementAndGet() % 100000 == 0) {
                logger.info { "Send '${counter.get()}' messages so far." }
            }
        }
//        events.forEach {
//            ProducerRecord(KafkaConfig.EVENTS_TOPIC, it.userSession, it)
//                .also { record ->
//                    producer.send(record) { metadata, exception ->
//                        exception?.let {
//                            logger.error { "Error while producing: $exception" }
//                        } ?: kotlin.run {
//                            metadata.show()
//                        }
//                    }
//                }
//            counter.getAndIncrement()
//        }

        producer.flush()
        logger.info("Total Event records sent: '${counter.get()}' in '${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - t0)}' seconds")

        logger.info("Closing Producers ...");
//        usersProducer.close()
//        productsProducer.close()
        producer.close()
    }
}