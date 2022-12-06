package io.ipolyzos.config

import io.ipolyzos.models.config.KafkaConnection
import org.apache.kafka.clients.consumer.ConsumerConfig
import java.util.*

object KafkaConfig {
    private val kafkaConfig: KafkaConnection = ConfigLoader.loadConfig().kafka

    const val USERS_TOPIC       = "ecommerce.users"
    const val PRODUCTS_TOPIC    = "ecommerce.products"
    const val EVENTS_TOPIC      = "ecommerce.events"

    private val ROOT_PATH: String           = System.getProperty("user.home", ".")
    private val CREDENTIALS_PATH: String    = "$ROOT_PATH/Documents/temp/"

    fun bootstrapServers(): String = kafkaConfig.servers
    fun password(): String = kafkaConfig.ssl.keyPassword

    fun buildConsumerProps(withSecurityProps: Boolean = true,
                           withSchemaRegistryProps: Boolean = true): Properties {
        val properties = Properties()
        properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG]         = kafkaConfig.servers

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