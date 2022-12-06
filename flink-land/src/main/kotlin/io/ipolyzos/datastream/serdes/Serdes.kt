package io.ipolyzos.datastream.serdes

import io.ipolyzos.models.clickstream.ClickEvent
import io.ipolyzos.models.clickstream.Product
import io.ipolyzos.models.clickstream.User
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema
import java.nio.charset.Charset

object Serdes {
    class EventDeSchema : AbstractDeserializationSchema<ClickEvent>() {
        override fun deserialize(message: ByteArray?): ClickEvent {
            return Json.decodeFromString(String(message!!, Charset.defaultCharset()))
        }
    }

    class UserDeSchema : AbstractDeserializationSchema<User>() {
        override fun deserialize(message: ByteArray?): User {
            return Json.decodeFromString(String(message!!, Charset.defaultCharset()))
        }
    }

    class ProductDeSchema : AbstractDeserializationSchema<Product>() {
        override fun deserialize(message: ByteArray?): Product {
            return Json.decodeFromString(String(message!!, Charset.defaultCharset()))
        }
    }
}