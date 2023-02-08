import io.ipolyzos.models.clickstream.ClickEvent
import io.ipolyzos.models.clickstream.Product
import io.ipolyzos.models.clickstream.User
import io.ipolyzos.utils.DataGenerator
import io.ipolyzos.utils.DataSourceUtils
import java.sql.Timestamp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

//import io.ipolyzos.config.AppConfig
//import org.apache.kafka.clients.admin.AdminClient
//import org.apache.kafka.clients.admin.ListOffsetsResult.ListOffsetsResultInfo
//import org.apache.kafka.clients.admin.OffsetSpec
//import org.apache.kafka.clients.consumer.ConsumerConfig
//import org.apache.kafka.clients.consumer.OffsetAndMetadata
//import org.apache.kafka.common.TopicPartition
//import org.apache.kafka.common.errors.UnknownMemberIdException
//import java.sql.Timestamp
//import java.util.*
//import java.util.concurrent.ExecutionException
//
//
//fun main(args: Array<String>) {
//    val properties = Properties()
//    properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = AppConfig.BOOTSTRAP_SERVERS
//    properties["security.protocol"] = "SSL"
//    properties["ssl.truststore.location"] = "${AppConfig.CREDENTIALS_PATH}/client.truststore.jks"
//    properties["ssl.truststore.password"] = "pass123"
//    properties["ssl.keystore.type"] = "PKCS12"
//    properties["ssl.keystore.location"] = "${AppConfig.CREDENTIALS_PATH}/client.keystore.p12"
//    properties["ssl.keystore.password"] = "pass123"
//    properties["ssl.key.password"] = "pass123"
//
//    val adminClient = AdminClient.create(properties)
//    adminClient.listTopics().listings().get()
//        .forEach {
//            println(it)
//        }
//
//    println("\nOffsets:")
//    adminClient.listConsumerGroupOffsets("parallel-group-0")
//        .all().get()
//        .forEach { println(it) }
//
//    println("\tTopics:")
//    adminClient.describeTopics(listOf("test.parallel.partitioned"))
//        .allTopicNames()
//        .get()
//        .forEach { println("\t$it") }
//
//
//
//    // Describe Consumer Groups
//    println("\nConsumer Groups:")
//    adminClient.listConsumerGroups().all().get()
//        .forEach {
//            println("\t$it")
//        }
//
//    adminClient.listConsumerGroups().valid().get()
//        .forEach {
//            println("\t$it")
//        }
//
//    // Describe a group
//    println("Describing Consumer Group:")
//    adminClient.describeConsumerGroups(listOf("parallel-group-0"))
//        .describedGroups()["parallel-group-0"]?.get()
//
//    // Get offsets committed by the group
//    val offsets = adminClient
//        .listConsumerGroupOffsets("parallel-group-0")
//        .partitionsToOffsetAndMetadata()
//        .get()
//
//    val cal = Calendar.getInstance()
//
//    cal.add(Calendar.DATE, -1)
//
//    val requestLatestOffsets = mutableMapOf<TopicPartition, OffsetSpec>()
//    val requestEarliestOffsets= mutableMapOf<TopicPartition, OffsetSpec>()
//    val requestOlderOffsets= mutableMapOf<TopicPartition, OffsetSpec>()
//
//    offsets.keys.forEach { k ->
//        requestLatestOffsets[k] = OffsetSpec.latest()
//        requestEarliestOffsets[k] = OffsetSpec.earliest()
//        requestOlderOffsets[k] = OffsetSpec.forTimestamp(cal.timeInMillis)
//    }
//
//    println("Latest Offsets:")
//    val latestOffsets = adminClient.listOffsets(requestLatestOffsets).all().get()
//    latestOffsets.forEach {
//        println("\t$it")
//    }
//
//    println("Earliest Offsets:")
//
//    val earliestOffsets =
//        adminClient.listOffsets(requestEarliestOffsets).all().get()
//    earliestOffsets.forEach {
//        println("\t$it")
//    }
//
//    // Reset offsets to beginning of topic. You can try to reset to 2h ago too by using `requestOlderOffsets`
//    val resetOffsets = mutableMapOf<TopicPartition, OffsetAndMetadata>()
//    earliestOffsets.forEach {
//        println("Will reset topic-partition " + it.key + " to offset " + it.value.offset())
//        resetOffsets[it.key] = OffsetAndMetadata(it.value.offset())
//    }
//
//    try {
//        adminClient.alterConsumerGroupOffsets("test.group-0", resetOffsets).all().get()
//    } catch (e: ExecutionException) {
//        println(
//            "Failed to update the offsets committed by group 'parallel-group-0'" +
//                    " with error " + e.message
//        )
//        if (e.cause is UnknownMemberIdException) println("Check if consumer group is still active.")
//    }
//
//    // Describe a group
//    println("Describing Consumer Group:")
//    adminClient.describeConsumerGroups(listOf("parallel-group-0"))
//        .all().get()
//        .forEach {
//            println("\t$it")
//        }
//
//    println("\nConsumer Groups:")
//    adminClient.listConsumerGroups().all().get()
//        .forEach {
//            println("\t$it")
//        }
//
//}

fun main() {
    while(true) {
        val reading = DataGenerator.generateSensorReading()
        println(DataGenerator.generateSensorInfo())
        println(Json.encodeToString(reading))
        Thread.sleep(500)
    }
}