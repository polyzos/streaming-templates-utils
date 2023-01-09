package io.ipolyzos.consumers

import io.ipolyzos.resources.ConsumerResource
import io.ipolyzos.config.KafkaConfig
import io.ipolyzos.models.clickstream.ClickEvent
import org.apache.kafka.clients.consumer.ConsumerConfig

fun main() {
    val properties = KafkaConfig.buildConsumerProps(
        "ecommerce.events.group"
    )
    properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
//    properties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "false"
//    properties[ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG] = 2000
//    properties[ConsumerConfig.FETCH_MIN_BYTES_CONFIG] = 10000
//    properties[ConsumerConfig.FETCH_MAX_BYTES_CONFIG] = 20001


    val consumerResource = ConsumerResource.live<String, ClickEvent>(properties)
    consumerResource.consume(KafkaConfig.EVENTS_TOPIC)
}