package io.ipolyzos.models.iot

import java.sql.Timestamp

data class SensorInfo(
    val id: String,
    val latitude: String,
    val longitude: String,
    val generation: Int,
    val deployed: Timestamp
)
