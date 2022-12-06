package io.ipolyzos.models.config


data class ConnectionsConfig(val kafka: KafkaConnection, val postgres: PostgresConnectionConfig)
