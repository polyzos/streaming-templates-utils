package io.ipolyzos.config

import io.confluent.kafka.serializers.KafkaJsonSerializer
import io.ipolyzos.models.config.KafkaConnection
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

object KafkaConfig {
    private val kafkaConfig: KafkaConnection = ConfigLoader.loadConfig().kafka

    const val USERS_TOPIC       = "ecommerce.users"
    const val PRODUCTS_TOPIC    = "ecommerce.products"
    const val EVENTS_TOPIC      = "ecommerce.events"

    private val ROOT_PATH: String       = System.getProperty("user.home", ".")
    val CREDENTIALS_PATH                = "$ROOT_PATH/Documents/temp/credentials"

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

    private fun withSecurityProps(properties: Properties): Properties {
        properties["security.protocol"]         = kafkaConfig.securityProtocol
        properties["ssl.truststore.location"]   = kafkaConfig.ssl.truststoreLocation
        properties["ssl.truststore.password"]   = kafkaConfig.ssl.truststorePassword
        properties["ssl.keystore.type"]         = kafkaConfig.ssl.keystoreType
        properties["ssl.keystore.location"]     = kafkaConfig.ssl.keystoreLocation
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

//    fun getSecurityProps(): Properties {
//        val properties = Properties()
//        properties["security.protocol"]         = kafkaConfig.securityProtocol
//        properties["ssl.truststore.location"]   = kafkaConfig.ssl.truststoreLocation
//        properties["ssl.truststore.password"]   = kafkaConfig.ssl.truststorePassword
//        properties["ssl.keystore.type"]         = kafkaConfig.ssl.keystoreType
//        properties["ssl.keystore.location"]     = kafkaConfig.ssl.keystoreLocation
//        properties["ssl.keystore.password"]     = kafkaConfig.ssl.keystorePassword
//        properties["ssl.key.password"]          = kafkaConfig.ssl.keystorePassword
//        return properties
//    }
}