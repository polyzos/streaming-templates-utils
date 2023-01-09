package io.ipolyzos.consumers

import io.ipolyzos.resources.ConsumerResource
import io.ipolyzos.config.KafkaConfig
import io.ipolyzos.models.clickstream.User
import org.apache.kafka.clients.consumer.ConsumerConfig

fun main() {
    val properties = KafkaConfig.buildConsumerProps(
        "ecommerce.users.group"
    )
    properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
    val consumerResource = ConsumerResource.live<String, User>(properties)
    consumerResource.consume(KafkaConfig.USERS_TOPIC)
}