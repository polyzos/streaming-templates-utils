package io.ipolyzos.models.iot

import kotlinx.serialization.Serializable

@Serializable
data class SensorReading(val id: String, val sensorType: String, val reading: Double)
