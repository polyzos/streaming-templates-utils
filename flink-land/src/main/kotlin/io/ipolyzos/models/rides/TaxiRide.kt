package io.ipolyzos.models.rides

import kotlinx.serialization.Serializable

@Serializable
data class TaxiRide(val rideId: String,
                    val vendorId: String,
                    val pickupTime: Long,
                    val dropoffTime: Long,
                    val pickupLocationId: String,
                    val dropoffLocationId: String,
                    val passengerCount: Int,
                    val tripDistance: Double,
                    val fareAmount: Double,
                    val tipAmount: Double,
                    val totalAmount: Double)
