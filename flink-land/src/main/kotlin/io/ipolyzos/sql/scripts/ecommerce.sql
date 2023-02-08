SELECT
    TUMBLE_START(eventTime_ltz, INTERVAL '30' SECONDS) AS startT,
    TUMBLE_END(eventTime_ltz, INTERVAL '30' SECONDS) AS endT,
    userSession,
    COLLECT(eventType) AS userSessionEventTypesCount,
    LISTAGG(eventType) AS events
FROM events
GROUP BY TUMBLE(eventTime_ltz, INTERVAL '30' SECONDS), userSession
-- END STATEMENT

-- INSERT INTO; AFTER APPLYING FILTER
INSERT INTO events_filtered
    SELECT eventTime, eventType, productId, categoryId, categoryCode, brand, price, userid, userSession
    FROM events
    WHERE eventType='purchase'


SELECT *
FROM events_filtered
    INNER JOIN users
        ON events_filtered.userid = users.userId

SELECT
    userSession,
    TUMBLE_START(`eventTime`, INTERVAL '1' HOUR) AS startT,
    TUMBLE_END(`eventTime`, INTERVAL '1' HOUR) AS endT,
    count(eventType)
FROM events
GROUP BY TUMBLE(`eventTime`, INTERVAL '1' HOUR), userSession
-- END STATEMENT


-- OR
CREATE VIEW eventusers AS
SELECT *
FROM events_filtered
         INNER JOIN users ON events_filtered.userid = users.userId

SELECT *
FROM eventusers
         INNER JOIN products ON products.productCode = eventusers.productId
-- END STATEMENT


-- Q: USER_SESSION_INTERACTIONS_PER_HOUR
SELECT
    userSession,
    TUMBLE_START(`eventTime`, INTERVAL '1' HOUR) AS startT,
    TUMBLE_END(`eventTime`, INTERVAL '1' HOUR) AS endT,
    count(eventType)
FROM events
GROUP BY TUMBLE(`eventTime`, INTERVAL '1' HOUR), userSession
-- END STATEMENT


-- CREATE TABLES
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


-- USER
userId      STRING,
firstname   STRING,
lastname    STRING,
username    STRING,
email       STRING,
title       STRING,
address     STRING

productCode STRING,
productColor STRING,
promoCode STRING,
productName STRING

eventTime   BIGINT,
productId   STRING,
price       DOUBLE,
userSession STRING,
firstname   STRING,
lastname    STRING,
email       STRING,
address     STRING

INSERT INTO eventusers
SELECT
    events_filtered.eventTime,
    events_filtered.productId,
    events_filtered.price,
    events_filtered.userSession,
    users.firstname,
    users.lastname,
    users.email,
    users.address
FROM events_filtered
         INNER JOIN users ON events_filtered.userid = users.userId

userSession STRING,
firstname   STRING,
lastname    STRING,
email       STRING,
address     STRING
price       DOUBLE,
productName MULTISET<STRING>
PRIMARY KEY (userSession) NOT ENFORCED

INSERT INTO events_enriched
SELECT eventusers.userSession,
       eventusers.firstname,
       eventusers.lastname,
       eventusers.email,
       eventusers.address,
       SUM(eventusers.price),
       COLLECT(products.productName)
FROM eventusers
         INNER JOIN products ON products.productCode = eventusers.productId
GROUP BY eventusers.userSession, eventusers.firstname, eventusers.lastname, eventusers.email, eventusers.address, products.productName
