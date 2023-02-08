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
        .executeSql("SELECT * FROM events LIMIT 10")
        .print()

//    tableEnvironment
//        .executeSql("""
//            INSERT INTO events_filtered
//            SELECT eventTime, eventType, productId, categoryId, categoryCode, brand, price, userid, userSession
//            FROM events
//            WHERE eventType='purchase'
//        """.trimIndent())

//        tableEnvironment
//            .executeSql("""
//                CREATE VIEW eventusers AS
//                SELECT events_filtered.eventTime, events_filtered.productId, events_filtered.userSession, users.firstname, users.lastname, users.address, events_filtered.price
//                FROM events_filtered
//                INNER JOIN users ON events_filtered.userid = users.userId
//                """)
////            .print()
////                GROUP BY events_filtered.productId, events_filtered.userSession, users.firstname, users.lastname, users.address
//
//    tableEnvironment
//        .executeSql("""
//                SELECT eventusers.userSession, eventusers.firstname, eventusers.lastname, eventusers.address, SUM(eventusers.price) , COLLECT(products.productName)
//                FROM eventusers
//                    INNER JOIN products ON products.productCode = eventusers.productId
//                GROUP BY eventusers.userSession, eventusers.firstname, eventusers.lastname, eventusers.address, products.productName
//                """)
//        .print()
}

