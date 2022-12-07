package io.ipolyzos.utils

import com.github.javafaker.Faker
import io.ipolyzos.models.iot.SensorReading
import io.ipolyzos.models.iot.SensorInfo
import io.ipolyzos.models.sim.FxTransaction
import io.ipolyzos.models.sim.Order
import io.ipolyzos.models.sim.Rate
import io.ipolyzos.models.sim.ServerLog
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object DataGenerator {
    private val faker = Faker()
    private val random = Random()

    fun generateServerLog(): ServerLog {
        val clientIp: String = faker.internet().publicIpV4Address()
        val clientIdentity: String = faker.internet().uuid()
        val userid: String = random.nextInt(10000).toString() + ""
        val userAgent: String = faker.expression("#{Internet.userAgentAny}")
        val logTime: Date = faker.date().past(15, 5, TimeUnit.SECONDS)
        val verb: String = faker.regexify("(GET|POST|PUT|PATCH){1}")
        val page: String =
            faker.regexify("(/search\\.html|/login\\.html|/prod\\.html|cart\\.html|/order\\.html){1}")
        val protocol: String = faker.regexify("(HTTP/1\\.1|HTTP/2|/HTTP/1\\.0){1}")
        val statusCode: String = faker.regexify("(200|201|204|400|401|403|301){1}")
        val size: Int = faker.number().numberBetween(100, 10000000)
        val requestLine = String.format("%s %s %s%n", verb, page, protocol)

        return ServerLog(
            clientIp = clientIp,
            clientIdentity =  clientIdentity,
            userid =  userid,
            userAgent =  userAgent,
            logTime = logTime.time,
            requestLine = requestLine,
            statusCode = statusCode,
            size = size
        )
    }


    fun generateOrder(): Order {
        val orderId: String = faker.number().numberBetween(0, 9999999).toString()
        val city: String = faker.address().city()
        val streetAddress: String = faker.address().streetAddress()
        val amount: Double = faker.number().numberBetween(0, 100).toString().toDouble()
        val productId: String = faker.number().numberBetween(0, 999999).toString()
        val orderStatus: String =
            faker.regexify("(RECEIVED|PREPARING|DELIVERING|DELIVERED|CANCELED){1}")

        return Order(
            orderId = orderId,
            city = city,
            streetAddress = streetAddress,
            amount = amount,
            productId = productId,
            orderTime = System.currentTimeMillis() - random.nextInt(10000) - 60 * 10000 * 5,
            orderStatus = orderStatus
        )
    }

    fun generateFxTransaction(): FxTransaction {
        val id: String = faker.internet().uuid()
        val currencyCode: String = faker.currency().code()
        val total: Double = faker.number().randomDouble(2, 10, 1000)
        val transactionTime: Date = faker.date().past(30, TimeUnit.SECONDS)
        return FxTransaction(
            id = id,
            currencyCode = currencyCode,
            total = total,
            transactionTime = transactionTime.time
        )
    }

    fun generateRate(): Rate {
        val currencyCode: String = faker.currency().code()
        val euroRate: Double = faker.number().randomDouble(2, 0, 10)
        val rateTime: Date = faker.date().past(15, TimeUnit.SECONDS)
        return Rate(
            currencyCode = currencyCode,
            euroRate =  euroRate,
            rateTime = rateTime.time
        )
    }


//    fun generateSensorReadings(numberRecords: Int): Map<String, MutableList<Sensor>>? {
//        val faker = Faker()
//        val idNumbers: MutableList<String> = ArrayList()
//        val idNumber = faker.idNumber()
//        for (i in 0..19) {
//            idNumbers.add(idNumber.invalid())
//        }
//        val numberPerTopic = numberRecords / 3
//        val number = faker.number()
//        val types: List<String> = listOf<String>(
//            "NONE",
//            "TEMPERATURE",
//            "PROXIMITY"
//        )
//        val recordsMap: MutableMap<String, MutableList<Sensor>> = HashMap()
//        val sensorTopics = listOf("combined-sensors", "temperature-sensors", "proximity-sensors")
//        sensorTopics.forEach(Consumer { sensorTopic: String? ->
//            recordsMap[sensorTopic!!] = ArrayList<Sensor>()
//        })
//        sensorTopics.forEach(Consumer { sensorTopic: String ->
//            val sensorList: MutableList<Sensor> = recordsMap[sensorTopic]!!
//            for (i in 0 until numberPerTopic) {
//                val type: String = when (sensorTopic) {
//                    "combined-sensors" -> types[number.numberBetween(0, 2)]
//                    "temperature-sensors" -> "TEMPERATURE"
//                    "proximity-sensors" -> "PROXIMITY"
//                    else -> types[number.numberBetween(0, 2)]
//                }
//                sensorList.add(
//                    Sensor(
//                        id=idNumbers[io.ipolyzos.ppc.random.nextInt(20)],
//                        sensorType=type,
//                        reading = number.randomDouble(2, 1, 1000)
//                    )
//                )
//            }
//        })
//        return recordsMap
//    }

    fun generateSensorReading(): SensorReading {
        val types: List<String> = listOf<String>(
            "TEMPERATURE",
            "PROXIMITY"
        )
        return SensorReading(
            id = faker.number().numberBetween(0, 11).toString(),
            sensorType = types[faker.number().numberBetween(0, 2)],
            reading = faker.number().randomDouble(2, 1, 300)
        )
    }

    fun generateSensorInfo(): SensorInfo {
        val maxIds = 10
        val idCounter = AtomicInteger(0)
        return SensorInfo(
            id = (idCounter.getAndIncrement() % maxIds).toString(),
            latitude = faker.address().latitude(),
            longitude = faker.address().longitude(),
            generation = faker.number().numberBetween(0, 4),
            deployed = Timestamp(faker.date().past(1825, TimeUnit.DAYS).time)
        )
    }
}