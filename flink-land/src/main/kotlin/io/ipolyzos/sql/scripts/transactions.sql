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
) WITH (
    'connector' = 'kafka',
    'topic' = 'transactions',
    'properties.bootstrap.servers' = 'localhost:9092',
    'properties.group.id' = 'txnGroup',
    'format' = 'json',
    'scan.startup.mode' = 'earliest-offset',
    'json.timestamp-format.standard' = 'ISO-8601',
    'json.fail-on-missing-field' = 'false',
    'json.ignore-parse-errors' = 'true'
)
-- END STATEMENT
CREATE TABLE accounts (
    accountId STRING,
    districtId DECIMAL,
    frequency STRING,
    `date` STRING
) WITH (
    'connector' = 'kafka',
    'topic' = 'accounts',
    'properties.bootstrap.servers' = 'localhost:9092',
    'properties.group.id' = 'accountGroup',
    'format' = 'json',
    'scan.startup.mode' = 'earliest-offset',
    'json.timestamp-format.standard' = 'ISO-8601',
    'json.fail-on-missing-field' = 'false',
    'json.ignore-parse-errors' = 'true'
-- END STATEMENT
SELECT
    TUMBLE_START(`eventTime_ltz`, INTERVAL '1' DAY) AS startT,
    TUMBLE_END(`eventTime_ltz`, INTERVAL '1' DAY) AS endT,
    COUNT(transactionId) AS txnSUM
FROM transactions GROUP BY TUMBLE(`eventTime_ltz`, INTERVAL '1' DAY)
-- END STATEMENT

-- Q: TOTAL_AMOUNT_PER_TRANSACTION
SELECT
    transactionId,
    SUM(amount) as amountTotal,
    COUNT(transactionId) AS txnSum
FROM transactions
GROUP BY transactionId
-- END STATEMENT

-- Q: HOW MANY TRANSACTIONS WE HAVE PER CUSTOMER?
SELECT
    customerId,
    COUNT(transactionId) AS txnSUM,
    COLLECT(accountId) AS Amounts,
    LISTAGG(transactionId, ',') AS aList
FROM transactions
GROUP BY customerId
-- END STATEMENT

SELECT *
FROM transactions
         INNER JOIN accounts ON transactions.accountId = accounts.accountId
-- END STATEMENT
