package io.ipolyzos.config

import io.confluent.kafka.serializers.KafkaJsonDeserializer
import io.confluent.kafka.serializers.KafkaJsonSerializer
import io.ipolyzos.models.config.KafkaConnection
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

object KafkaConfig {
    private val kafkaConfig: KafkaConnection = ConfigLoader.loadConfig().kafka

    const val USERS_TOPIC       = "ecommerce.users"
    const val PRODUCTS_TOPIC    = "ecommerce.products"
    const val EVENTS_TOPIC      = "ecommerce.events"

    private val ROOT_PATH: String           = System.getProperty("user.home", ".")
    private val CREDENTIALS_PATH: String    = "$ROOT_PATH/Documents/temp/"

    fun bootstrapServers(): String = kafkaConfig.servers

    fun buildProducerProps(withSecurityProps: Boolean = true, withSchemaRegistryProps: Boolean = true): Properties {
        val properties = Properties()
        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG]      = kafkaConfig.servers
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG]   = StringSerializer::class.java.canonicalName
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaJsonSerializer::class.java.canonicalName
        properties[ProducerConfig.ACKS_CONFIG]                   = "1"

        properties[ProducerConfig.BATCH_SIZE_CONFIG]        = "64000"
        properties[ProducerConfig.LINGER_MS_CONFIG]         = "20"
        properties[ProducerConfig.COMPRESSION_TYPE_CONFIG]  = "gzip"

//        // Retries
//        properties[ProducerConfig.RETRIES_CONFIG] = 2147483647 // Users should generally prefer to leave this config unset and instead use delivery.timeout.ms to control retry behavior.
//        properties[ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG] = 120000 // (2 minutes)
//
//        properties[ProducerConfig.MAX_BLOCK_MS_CONFIG] = 60000 // (1 minute)
//        // For send() this timeout bounds the total time waiting for both metadata fetch and buffer allocation (blocking in the user-supplied serializers or partitioner is not counted against this timeout).
//        // For partitionsFor() this timeout bounds the time spent waiting for metadata if it is unavailable.
//
//        properties[ProducerConfig.MAX_REQUEST_SIZE_CONFIG] = 2048576 //Default: 1MB // The maximum size of a request in bytes. This setting will limit the number of record batches the producer will send
//        in a single request to avoid sending huge requests. This is also effectively a cap on the maximum uncompressed record batch size
//        properties[ProducerConfig.PARTITIONER_CLASS_CONFIG] = "null"
//
//        properties[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = "true" //Note that enabling idempotence requires max.in.flight.requests.per.connection to be less than or equal to 5 (with message ordering preserved for any allowable value), retries to be greater than 0, and acks must be ‘all’.
//        properties[ProducerConfig.INTERCEPTOR_CLASSES_CONFIG] = "" // Implementing the org.apache.kafka.clients.producer.ProducerInterceptor
//
//        properties[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = 10

        if (withSecurityProps) withSecurityProps(properties) else properties
        return if (withSchemaRegistryProps) withRegistryConfig(properties) else properties
    }

    fun buildConsumerProps(groupId: String,
                           autoCommit: Boolean = true,
                           withSecurityProps: Boolean = true,
                           withSchemaRegistryProps: Boolean = true): Properties {
        val properties = Properties()
        properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG]         = kafkaConfig.servers
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG]    = StringDeserializer::class.java.canonicalName
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG]  = KafkaJsonDeserializer::class.java.canonicalName
        properties[ConsumerConfig.GROUP_ID_CONFIG]                  = groupId
        properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG]         = "latest"
        properties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG]        = autoCommit.toString()

//        properties[ConsumerConfig.MAX_POLL_RECORDS_CONFIG]          = ""// 500
//        properties[ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG]      = ""//300000 (5 minutes)
//
//        properties[ConsumerConfig.RECEIVE_BUFFER_CONFIG]      = ""  // 65536 (64 kibibytes)
//        properties[ConsumerConfig.FETCH_MAX_BYTES_CONFIG]      = ""//52428800 (50 mebibytes)
//        properties[ConsumerConfig.FETCH_MIN_BYTES_CONFIG]      = ""// 1 Byte  autoCommit.toString()
//        properties[ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG]      = "" // 500ms 1 Byte  autoCommit.toString()

        if (withSecurityProps) withSecurityProps(properties) else properties
        return if (withSchemaRegistryProps) withRegistryConfig(properties) else properties    }

    private fun withSecurityProps(properties: Properties): Properties {
        properties["security.protocol"]         = kafkaConfig.securityProtocol
        properties["ssl.truststore.location"]   = CREDENTIALS_PATH + kafkaConfig.ssl.truststoreLocation
        properties["ssl.truststore.password"]   = kafkaConfig.ssl.truststorePassword
        properties["ssl.keystore.type"]         = kafkaConfig.ssl.keystoreType
        properties["ssl.keystore.location"]     = CREDENTIALS_PATH + kafkaConfig.ssl.keystoreLocation
        properties["ssl.keystore.password"]     = kafkaConfig.ssl.keystorePassword
        properties["ssl.key.password"]          = kafkaConfig.ssl.keystorePassword
        return properties
    }

    private fun withRegistryConfig(properties: Properties): Properties {
        properties["schema.registry.url"]           = kafkaConfig.registry.url
        properties["basic.auth.credentials.source"] = kafkaConfig.registry.credentialsSource
        properties["basic.auth.user.info"]          = kafkaConfig.registry.authUserInfo
        return properties
    }
}