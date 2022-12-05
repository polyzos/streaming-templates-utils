package io.ipolyzos.consumers

import io.ipolyzos.ConsumerResource
import io.ipolyzos.config.KafkaConfig
import io.ipolyzos.models.clickstream.ClickEvent
import mu.KLogger
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig

private val logger: KLogger by lazy { KotlinLogging.logger {} }

fun main() {
    val properties = KafkaConfig.buildConsumerProps(
        "ecommerce.events.group2"
    )
    properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
//    properties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "false"
//    properties[ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG] = 2000
//    properties[ConsumerConfig.FETCH_MIN_BYTES_CONFIG] = 10000
//    properties[ConsumerConfig.FETCH_MAX_BYTES_CONFIG] = 20001


    val consumerResource = ConsumerResource.live<String, ClickEvent>(properties)
    logger.info { "Starting consumer with properties:" }

    consumerResource.consume(KafkaConfig.EVENTS_TOPIC)
}