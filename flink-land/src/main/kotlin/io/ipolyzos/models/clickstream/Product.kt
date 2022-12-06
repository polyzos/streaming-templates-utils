package io.ipolyzos.models.clickstream

import kotlinx.serialization.Serializable

@Serializable
data class Product(val productCode: String,
                   val productColor: String,
                   val promoCode: String,
                   val productName: String)
