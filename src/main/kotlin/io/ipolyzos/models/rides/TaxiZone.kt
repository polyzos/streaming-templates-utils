package io.ipolyzos.models.rides

import kotlinx.serialization.Serializable

@Serializable
data class TaxiZone(val locationID: String, val borough: String, val zone: String, val serviceZone: String)