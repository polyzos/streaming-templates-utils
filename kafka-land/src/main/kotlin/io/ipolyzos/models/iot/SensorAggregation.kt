package io.ipolyzos.models.iot

data class SensorAggregation(val sensorId: String, val startTime: String, val endTime: String, val readings: List<Double>, val averageTemp: Double)
