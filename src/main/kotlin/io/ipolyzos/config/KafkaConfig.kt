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

    private val ROOT_PATH: String       = System.getProperty("user.home", ".")
    val CREDENTIALS_PATH                = "$ROOT_PATH/Documents/temp/"

    fun bootstrapServers(): String = kafkaConfig.servers

    fun buildProducerProps(withSecurityProps: Boolean = true, withSchemaRegistryProps: Boolean = true): Properties {
        val properties = Properties()
        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG]      = kafkaConfig.servers
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG]   = StringSerializer::class.java.canonicalName
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaJsonSerializer::class.java.canonicalName
        properties[ProducerConfig.ACKS_CONFIG]                   = "1"
//        properties[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION]                   = "10"
//        properties[ProducerConfig.BATCH_SIZE_CONFIG]    = "1000000"
//        properties[ProducerConfig.LINGER_MS_CONFIG]     = "100"

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