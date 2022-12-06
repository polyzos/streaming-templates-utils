package io.ipolyzos.utils

import com.github.javafaker.Faker
import io.ipolyzos.models.sim.FxTransaction
import io.ipolyzos.models.sim.Order
import io.ipolyzos.models.sim.Rate
import io.ipolyzos.models.sim.ServerLog
import java.util.*
import java.util.concurrent.TimeUnit

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
}