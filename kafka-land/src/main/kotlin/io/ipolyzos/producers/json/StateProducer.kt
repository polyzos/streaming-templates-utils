package io.ipolyzos.producers.json

import io.ipolyzos.resources.ProducerResource
import io.ipolyzos.config.KafkaConfig
import io.ipolyzos.models.clickstream.Product
import io.ipolyzos.models.clickstream.User
import io.ipolyzos.utils.DataSourceUtils
import mu.KLogger
import mu.KotlinLogging
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

fun main() = StateProducer.runProducer()

object StateProducer {
    private val logger: KLogger by lazy { KotlinLogging.logger {} }

    fun runProducer() {
        val users: Sequence<User> = DataSourceUtils
            .loadDataFile("/Documents/data/clickevents/users.csv", DataSourceUtils.toUser)

        val products: Sequence<Product> = DataSourceUtils
            .loadDataFile("/Documents/data/clickevents/products.csv", DataSourceUtils.toProduct)

        val properties = KafkaConfig.buildProducerProps()

        val userResource: ProducerResource<String, User> = ProducerResource.live<String, User>(properties)
        var time = measureTimeMillis {
            for (user in users) {
                userResource.produce(KafkaConfig.USERS_TOPIC, user.userId, user)
            }
        }

        userResource.shutdown()
        logger.info("Total time '${TimeUnit.MILLISECONDS.toSeconds(time)}' seconds")

        val productResource: ProducerResource<String, Product> = ProducerResource.live<String, Product>(properties)
        time = measureTimeMillis {
            for (product in products) {
                productResource.produce(KafkaConfig.PRODUCTS_TOPIC, product.productCode, product)
            }
        }

        productResource.shutdown()
        logger.info("Total time '${TimeUnit.MILLISECONDS.toSeconds(time)}' seconds")

    }
}