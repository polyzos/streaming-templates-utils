package io.ipolyzos.models.sim

data class Order(val orderId: String,
                 val city: String,
                 val streetAddress: String,
                 val amount: Double,
                 val productId: String,
                 val orderTime: Long,
                 val orderStatus: String)
