package io.ipolyzos.sql


object Queries {
    // Ecommerce Queries
    var USER_SESSION_EVENT_TYPES_COUNT = """
        SELECT userSession, COLLECT(eventType) as userSessionEventTypesCount, LISTAGG(eventType, ',') 
        FROM click_events 
        GROUP BY userSession
    """.trimIndent()

    val query = """
        SELECT 
            TUMBLE_START(`eventTime_ltz`, INTERVAL '90' DAY) AS startT,
            TUMBLE_END(`eventTime_ltz`, INTERVAL '90' DAY) AS endT,
            COUNT(transactionId) AS txnSUM
        FROM transactions GROUP BY TUMBLE(`eventTime_ltz`, INTERVAL '90' DAY)
    """.trimIndent()


    val TOTAL_AMOUNT_PER_TRANSACTION = """
        SELECT 
            transactionId,
            SUM(amount) as amountTotal,
            COUNT(transactionId) AS txnSum
        FROM transactions
        GROUP BY transactionId
    """.trimIndent()

    // HOW MANY TRANSACTIONS WE HAVE PER CUSTOMER?
    val TRANSACTIONS_PER_CUSTOMER = """
        SELECT
            customerId,
            COUNT(transactionId) AS txnSUM,
            COLLECT(accountId) AS Amounts, 
            LISTAGG(transactionId, ',') AS aList 
        FROM transactions 
        GROUP BY customerId
    """.trimIndent()
}