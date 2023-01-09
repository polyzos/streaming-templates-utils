package io.ipolyzos.consumers

import io.ipolyzos.resources.ConsumerResource
import io.ipolyzos.config.KafkaConfig
import io.ipolyzos.models.clickstream.User
import org.apache.kafka.clients.consumer.ConsumerConfig
import kotlin.concurrent.thread

fun main() = IConsumer.runConsumer()

object IConsumer {
    fun runConsumer() {
        val properties = KafkaConfig.buildConsumerProps(
            "ecommerce.users.parallel.group"
        )
        properties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "false"
        properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        val numConsumers = 1
        (1..numConsumers).forEach { _ ->
            thread {
                val consumerResource = ConsumerResource.live<String, User>(properties)
                consumerResource.consumeParallel(KafkaConfig.USERS_TOPIC)
            }
        }
    }
}
