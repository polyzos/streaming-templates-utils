package io.ipolyzos.sql

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

fun main() {
    val environment = StreamExecutionEnvironment
        .createLocalEnvironmentWithWebUI(Configuration())

    environment.parallelism = 5

    val tableEnvironment = StreamTableEnvironment.create(environment)

    // Run some SQL queries to check the existing Catalogs, Databases and Tables
    tableEnvironment
        .executeSql("SHOW CATALOGS")
        .print()

    tableEnvironment
        .executeSql("SHOW DATABASES")
        .print()

    tableEnvironment
        .executeSql("SHOW TABLES")
        .print()

    tableEnvironment
        .executeSql(CreateTable.EVENTS)
        .print()


    tableEnvironment
        .executeSql(CreateTable.PRODUCTS)
        .print()

    tableEnvironment
        .executeSql(CreateTable.USERS)
        .print()

    tableEnvironment
        .executeSql(CreateTable.EVENTS_FILTERED)
        .print()


    tableEnvironment
        .executeSql("""
            INSERT INTO events_filtered
            SELECT eventTime, eventType, productId, categoryId, categoryCode, brand, price, userid, userSession
            FROM events
            WHERE eventType='purchase'
        """.trimIndent())

        tableEnvironment
            .executeSql("""
                CREATE VIEW eventusers AS
                SELECT *
                FROM events_filtered
                INNER JOIN users ON events_filtered.userid = users.userId
                """)

    tableEnvironment
        .executeSql("""
                SELECT * 
                FROM eventusers
                INNER JOIN products ON products.productCode = eventusers.productId
                """)
        .print()
}

