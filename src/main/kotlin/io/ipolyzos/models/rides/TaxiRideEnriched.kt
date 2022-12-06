package io.ipolyzos.models.rides

import kotlinx.serialization.Serializable

@Serializable
data class TaxiRideEnriched(val taxiRide: TaxiRide, val taxiZone: TaxiZone)