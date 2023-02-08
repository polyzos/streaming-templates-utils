package io.ipolyzos.sql

import io.ipolyzos.config.KafkaConfig

object CreateTable {
    private val ROOT_PATH: String           = System.getProperty("user.home", ".")
    private val CREDENTIALS_PATH: String    = "$ROOT_PATH/Documents/temp/"

    private fun connectorWith(topic: String, groupId: String): String {
        return """
            WITH (
            'connector' = 'kafka',
            'topic' = '$topic',
            'properties.bootstrap.servers' = '${KafkaConfig.bootstrapServers()}',
            'properties.group.id' = '$groupId',
            'format' = 'json',
            'scan.startup.mode' = 'earliest-offset',
            'json.timestamp-format.standard' = 'ISO-8601',
            'json.fail-on-missing-field' = 'false',
            'json.ignore-parse-errors' = 'true',
            'properties.security.protocol' = 'SSL',
            'properties.ssl.truststore.location' = '${CREDENTIALS_PATH}credentials/client.truststore.jks',
            'properties.ssl.truststore.password' = '${KafkaConfig.password()}',
            'properties.ssl.keystore.type' = 'PKCS12',
            'properties.ssl.keystore.location' = '${CREDENTIALS_PATH}credentials/client.keystore.p12',
            'properties.ssl.keystore.password' = '${KafkaConfig.password()}',
            'properties.ssl.key.password' = '${KafkaConfig.password()}'
        )
        """.trimIndent()
    }
    val EVENTS = """
        CREATE TABLE events (
            eventTime BIGINT,
            eventTime_ltz AS TO_TIMESTAMP_LTZ(eventTime, 3),
            eventType STRING,
            productId STRING,
            categoryId STRING,
            categoryCode STRING,
            brand STRING,
            price DOUBLE,
            userid STRING,
            userSession STRING,
                WATERMARK FOR eventTime_ltz AS eventTime_ltz - INTERVAL '5' SECONDS
        ) ${connectorWith("ecommerce.events", "ecommerce.events.sql.group")}
    """.trimIndent()

    val EVENTS_FILTERED = """
        CREATE TABLE events_filtered (
                eventTime BIGINT,
                eventTime_ltz AS TO_TIMESTAMP_LTZ(eventTime, 3),
                eventType STRING,
                productId STRING,
                categoryId STRING,
                categoryCode STRING,
                brand STRING,
                price DOUBLE,
                userid STRING,
                userSession STRING,
                    WATERMARK FOR eventTime_ltz AS eventTime_ltz - INTERVAL '5' SECONDS
        ) ${connectorWith("ecommerce.events.filtered", "ecommerce.events.filtered.sql.group")}
    """.trimIndent()

    val USERS = """
        CREATE TABLE users (
                userId      STRING,
                firstname   STRING,
                lastname    STRING,
                username    STRING,
                email       STRING,
                title       STRING,
                address     String
        ) ${connectorWith("ecommerce.users", "ecommerce.users.sql.group")}
    """.trimIndent()

    val PRODUCTS = """
        CREATE TABLE products (
            productCode STRING,
            productColor STRING,
            promoCode STRING,
            productName STRING
        ) ${connectorWith("ecommerce.products", "ecommerce.products.sql.group")}
    """.trimIndent()

    val userEvents = """
        eventTime BIGINT,
        productId STRING,
        price DOUBLE,
        userSession STRING,
        firstname   STRING,
        lastname    STRING,
        address     String
    """.trimIndent()

    //             eventTime_ltz AS TO_TIMESTAMP_LTZ(eventTime, 3),
    //          WATERMARK FOR eventTime_ltz AS eventTime_ltz - INTERVAL '15' MINUTES
    val CREATE_TXN_TABLE = """
        CREATE TABLE transactions (
            transactionId STRING,
            accountId STRING,
            customerId STRING,
            eventTime TIMESTAMP_LTZ(3),
            type STRING,
            operation STRING,
            amount DECIMAL,
            balance DECIMAL,
            symbol STRING,
                WATERMARK FOR eventTime AS eventTime - INTERVAL '5' MINUTES
        ) ${connectorWith("finance.txn", "finance.txn.sql.group")}
    """.trimIndent()

    //
    val CREATE_ACCOUNTS_TABLE = """
        CREATE TABLE accounts (
            accountId STRING,
            districtId DECIMAL,
            frequency STRING,
            `date` STRING
        ) ${connectorWith("finance.accounts", "finance.accounts.sql.group")}
    """.trimIndent()


    val CREATE_CUSTOMERS_TABLE = """
        CREATE TABLE customers (
            customerId STRING,
            sex STRING,
            social STRING,
            fullName STRING,
            phone STRING,
            email STRING,
            address1 STRING,
            address2 STRING,
            city STRING,
            state STRING,
            zipcode STRING,
            districtId STRING,
            birthDate STRING
        ) ${connectorWith("orders.customers", "orders.customers.sql.group")}
    """.trimIndent()

    val CREATE_ORDERS_TABLE = """
        CREATE TABLE IF NOT EXISTS orders (
            orderId STRING,
            city STRING,
            streetAddress STRING,
            amount DECIMAL,
            productId STRING,
            orderTime BIGINT,
            orderStatus STRING,
            orderTime_ltz AS TO_TIMESTAMP_LTZ(orderTime, 3),
            `record_time` TIMESTAMP_LTZ(3) METADATA FROM 'timestamp',
                WATERMARK FOR orderTime_ltz AS orderTime_ltz - INTERVAL '5' SECONDS
        ) ${connectorWith("orders", "orders.sql.group")}
    """.trimIndent()

    val CREATE_SERVERLOGS_TABLE = """
        CREATE TABLE server_logs (
            clientIp STRING,
            clientIdentity STRING,
            userid STRING,
            userAgent STRING,
            logTime TIMESTAMP_LTZ(3),
            requestLine STRING,
            statusCode STRING,
            size INT,
                WATERMARK FOR logTime AS logTime - INTERVAL '1' SECONDS
        ) ${connectorWith("server.logs", "server.logs.sql.group")}
    """.trimIndent()

    //                 WATERMARK FOR eventTime AS eventTime - INTERVAL '5' MINUTES
    val CREATE_SENSOR_READINGS_TABLE = """
        CREATE TABLE sensors (
            sensorId STRING,
            eventTime BIGINT,
            eventTime_ltz AS TO_TIMESTAMP_LTZ(eventTime, 3),
            `ts` TIMESTAMP(3) METADATA FROM 'timestamp',
            reading DECIMAL,
                WATERMARK FOR eventTime_ltz AS eventTime_ltz - INTERVAL '5' SECONDS
        ) ${connectorWith("sensors", "sensors.sql.group")}
    """.trimIndent()
}