package io.ipolyzos.utils

import io.ipolyzos.models.clickstream.ClickEvent
import io.ipolyzos.models.clickstream.Product
import io.ipolyzos.models.clickstream.User
import java.io.File
import java.sql.Timestamp

object DataSourceUtils {
    fun <T> loadDataFile(filename: String, fn: (x: String) -> T, withHeader: Boolean = true): Sequence<T> {
        val lines = File(System.getProperty("user.home") + filename)
            .inputStream()
            .bufferedReader()
            .lineSequence()

        return if (withHeader) lines.drop(1).map(fn) else lines.map(fn)
    }

    val toEvent: (String) -> ClickEvent = { line: String ->
        val tokens = line.split(",")
        val timestamp = Timestamp.valueOf(tokens[0].replace(" UTC", ""))

        ClickEvent(
            eventTime    = timestamp.time,
            eventType    = tokens[1],
            productId    = tokens[2],
            categoryId   = tokens[3],
            categoryCode = tokens[4],
            brand        = tokens[5],
            price        = tokens[6],
            userid       = tokens[7],
            userSession = tokens[8]
        )
    }

    val toProduct: (String) -> Product = { line: String ->
        val tokens = line.split(",")
        Product(
            productCode  = tokens[0],
            productColor = tokens[1],
            promoCode    = tokens[2],
            productName  = tokens[3]
        )
    }

    val toUser: (String) -> User = { line: String ->
        val tokens = line.split(",")
        User(
            userId    = tokens[0],
            firstname = tokens[1],
            lastname  = tokens[2],
            username  = tokens[3],
            title     = tokens[4],
            address   = tokens[5]
        )
    }
}