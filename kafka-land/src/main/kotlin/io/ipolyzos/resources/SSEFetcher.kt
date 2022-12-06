package io.ipolyzos.resources

import io.ipolyzos.models.sse.RCFeedEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import mu.KLogger
import mu.KotlinLogging
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.sql.Timestamp
import java.util.concurrent.atomic.AtomicInteger

private val logger: KLogger by lazy { KotlinLogging.logger {} }

data class Event(val name: String = "", val data: String = "")
data class Event2(val event: String = "", val id: String = "", val data: RCFeedEvent)

fun main() = runBlocking {
    getEventFlow2()
        .collect { result ->
            val event = RCEvent(
                result.id ?: -1,
                result.type,
                result.namespace,
                result.title,
                result.comment,
                result.timestamp,
                result.user,
                result.bot)

            println("[${Timestamp(event.timestamp)}]: $event")
            withContext(Dispatchers.IO) {
                Thread.sleep(200)
            }
        }
}

fun getEventFlow(): Flow<Event> = flow {
    coroutineScope {
        val conn = getStreamConnection("https://hacker-news.firebaseio.com/v0/updates.json")
        val input = conn.inputStream.bufferedReader()

        try {
            withContext(Dispatchers.IO) {
                conn.connect()
            }
            var event = Event()

            while(isActive) {
                val line = withContext(Dispatchers.IO) {
                    input.readLine()
                }
                when {
                    line.startsWith("event:") -> {
                        event = event.copy(name = line.substring(6).trim())
                    }
                    line.startsWith("data:") -> {
                        val data = line.substring(5).trim()
                            event = event.copy(data = data)

                    }
                    line.isEmpty() -> {
                        emit(event)
                        event = Event()
                    }
                }
            }
        } catch (e: IOException) {
            this.cancel(CancellationException("Network problem: $e"))
        } finally {
            conn.disconnect()
            withContext(Dispatchers.IO) {
                input.close()
            }
        }
    }
}


// https://stream.wikimedia.org/v2/stream/mediawiki.page-create
//

data class RCEvent(val id: Int,
                   val type: String,
                   val namespace: Int,
                   val title: String,
                   val comment: String,
                   val timestamp: Long,
                   val user: String,
                   val bot: Boolean)

fun getEventFlow2(): Flow<RCFeedEvent> = flow {
    coroutineScope {
        val conn = getStreamConnection("https://stream.wikimedia.org/v2/stream/recentchange")
        val input = conn.inputStream.bufferedReader()

        val count = AtomicInteger()
        val entries = mutableMapOf<String, Int>()
        try {
            withContext(Dispatchers.IO) {
                conn.connect()
            }

            while(isActive) {
                val line = withContext(Dispatchers.IO) {
                    input.readLine()
                }

                if (line.startsWith("data:")) {
                    println(line)
                    val input: String = line
                        .substring(5)
                        .trim()
                        .replace("$", "")

                    val result = if (input.contains("log_params")) {
                        val json=Json.parseToJsonElement(input)

//                        val id: Int = json.jsonObject.getOrDefault("id", -1) as Int
//                        val type: String = json.jsonObject.getOrDefault("type", "").toString()
//                        val namespace: Int = json.jsonObject.getOrDefault("namespace", -1) as Int
//                        val title: String = json.jsonObject.getOrDefault("title", "").toString()
//                        val comment: String = json.jsonObject.getOrDefault("comment", "").toString()
//                        val timestamp: Long = json.jsonObject.getOrDefault("timestamp", -1) as Long
//                        val user: String = json.jsonObject.getOrDefault("user", "").toString()
//                        val bot: Boolean = json.jsonObject.getOrDefault("bot", false) as Boolean
//
//                        val event = RCEvent(id, type, namespace, title, comment, timestamp, user, bot)
//                        println(event)
                        json.jsonObject
                            .filter { it.key == "log_params" }
                            .map {
                                val str = line!!.replace("${it.value}","""
                                    "${it.value.toString().replace("\"", "")}"
                                    """.trimIndent())
                                    .substring(5)
                                    .trim()
                                    .replace("$", "")
                                Json.decodeFromString<RCFeedEvent>(str)
                            }[0]
                    } else {
                        Json.decodeFromString<RCFeedEvent>(input)
                    }

                    emit(result)
                }
            }
        } catch (e: IOException) {
            this.cancel(CancellationException("Network problem: $e"))
        } finally {
            conn.disconnect()
            withContext(Dispatchers.IO) {
                input.close()
            }
        }
    }
}

private suspend fun getStreamConnection(url: String): HttpURLConnection =
    withContext(Dispatchers.IO) {
        return@withContext(URL(url).openConnection() as HttpURLConnection).also {
            it.setRequestProperty("Accept", "text/event-stream")
            it.doInput = true
        }
    }


