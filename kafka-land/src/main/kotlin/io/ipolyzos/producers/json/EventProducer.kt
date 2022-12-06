package io.ipolyzos.producers.json

import io.ipolyzos.resources.ProducerResource
import io.ipolyzos.config.KafkaConfig
import io.ipolyzos.models.clickstream.ClickEvent
import io.ipolyzos.utils.DataSourceUtils
import mu.KLogger
import mu.KotlinLogging
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

fun main() = ECommerceProducer.runProducer()

object ECommerceProducer {
    private val logger: KLogger by lazy { KotlinLogging.logger {} }

    fun runProducer() {
        val events: Sequence<ClickEvent> = DataSourceUtils
            .loadDataFile("/Documents/data/clickevents/events.csv", DataSourceUtils.toEvent)
            .take(1000000)

        val properties = KafkaConfig.buildProducerProps()

        val producerResource: ProducerResource<String, ClickEvent> = ProducerResource.live<String, ClickEvent>(properties)

        val time = measureTimeMillis {
            for (event in events) {
                producerResource.produce(KafkaConfig.EVENTS_TOPIC, event.userSession, event)
            }
        }

        producerResource.shutdown()
        logger.info("Total time '${TimeUnit.MILLISECONDS.toSeconds(time)}' seconds")
    }
}