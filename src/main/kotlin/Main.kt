import io.ipolyzos.config.ConfigLoader
import io.ipolyzos.models.clickstream.ClickEvent

import io.ipolyzos.utils.DataSourceUtils

import mu.KLogger
import mu.KotlinLogging
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime


val logger: KLogger by lazy { KotlinLogging.logger {} }

fun main(args: Array<String>) {

    val config = ConfigLoader.loadConfig()
    println(config)

            val events: Sequence<ClickEvent> = DataSourceUtils
            .loadDataFile("/Documents/data/clickevents/events.csv", DataSourceUtils.toEvent)

//        val products: List<Product> = DataSourceUtils
//            .loadDataFile("/Documents/data/clickevents/products.csv", DataSourceUtils.toProduct, withHeader = false)

//        val users: List<User> = DataSourceUtils
//            .loadDataFile("/Documents/data/clickevents/users.csv", DataSourceUtils.toUser, withHeader = false)

    val time = measureTimeMillis {
        events.forEach {
            logger.info { it }
        }
    }

    logger.info { "Read file in ${TimeUnit.MILLISECONDS.toSeconds(time)}" }

    val events2: Sequence<ClickEvent> = DataSourceUtils
        .loadDataFile("/Documents/data/clickevents/events.csv", DataSourceUtils.toEvent)
    logger.info { events2.count() }
}