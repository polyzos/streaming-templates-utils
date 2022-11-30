//package io.ipolyzos.producers
//
//import io.confluent.kafka.serializers.KafkaJsonSerializer
//import io.ipolyzos.models.clickstream.ClickEvent
//import io.ipolyzos.models.clickstream.Product
//import io.ipolyzos.models.clickstream.User
//import io.ipolyzos.utils.DataSourceUtils
//import org.apache.kafka.clients.producer.KafkaProducer
//import org.apache.kafka.clients.producer.ProducerConfig
//import org.apache.kafka.clients.producer.ProducerRecord
//import org.apache.kafka.common.serialization.StringSerializer
//import java.util.*
//import java.util.concurrent.TimeUnit
//import java.util.concurrent.atomic.AtomicInteger
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//fun main() = ECommerceProducer.runProducer()
//
//object ECommerceProducer {
//    private val logger: Logger
//            = LoggerFactory.getLogger(ECommerceProducer::class.java.canonicalName)
//
//    fun runProducer() {
//        val events: List<ClickEvent> = DataSourceUtils
//            .loadDataFile("/data/clickevents/events.csv", DataSourceUtils.toEvent)
//
//        val products: List<Product> = DataSourceUtils
//            .loadDataFile("/data/clickevents/products.csv", DataSourceUtils.toProduct, withHeader = false)
//
//        val users: List<User> = DataSourceUtils
//            .loadDataFile("/data/clickevents/users.csv", DataSourceUtils.toUser, withHeader = false)
//
//        val properties = Properties()
//        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = AppConfig.BOOTSTRAP_SERVERS
//        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.canonicalName
//        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaJsonSerializer::class.java.canonicalName
//        properties[ProducerConfig.ACKS_CONFIG] = "1"
//
//        properties["security.protocol"] = "SSL"
//        properties["ssl.truststore.location"] = "credentials/client.truststore.jks"
//        properties["ssl.truststore.password"] = "pass123"
//        properties["ssl.keystore.type"] = "PKCS12"
//        properties["ssl.keystore.location"] = "credentials/client.keystore.p12"
//        properties["ssl.keystore.password"] = "pass123"
//        properties["ssl.key.password"] = "pass123"
//
//        logger.info("Starting Kafka Producers with configs ...")
//        properties.forEach { logger.info("\t${it.key}: ${it.value}") }
//
//        val eventsProducer = KafkaProducer<String, ClickEvent>(properties)
//        val productsProducer = KafkaProducer<String, Product>(properties)
//        val usersProducer = KafkaProducer<String, User>(properties)
//
//        val counter = AtomicInteger(0)
//        val t0 = System.currentTimeMillis()
//
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
//
//        events.forEach {
//            val record: ProducerRecord<String, ClickEvent> =
//                ProducerRecord<String, ClickEvent>(AppConfig.EVENTS_TOPIC, it.userSesssion, it)
//            eventsProducer.send(record)
//            counter.getAndIncrement()
//        }
//
//        eventsProducer.flush()
//        logger.info("Total Event records sent: '${counter.get()}' in '${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - t0)}' seconds")
//        counter.set(0)
//
//        logger.info("Closing Producers ...");
//        usersProducer.close()
//        productsProducer.close()
//        eventsProducer.close()
//    }
//}