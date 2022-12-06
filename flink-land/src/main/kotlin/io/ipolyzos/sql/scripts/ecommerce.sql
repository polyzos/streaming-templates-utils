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
