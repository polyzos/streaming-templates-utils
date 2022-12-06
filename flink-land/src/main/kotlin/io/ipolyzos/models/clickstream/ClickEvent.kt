package io.ipolyzos.models.clickstream

import kotlinx.serialization.Serializable

@Serializable
data class ClickEvent(val eventTime: Long,
                      val eventType: String,
                      val productId: String,
                      val categoryId: String,
                      val categoryCode: String,
                      val brand: String,
                      val price: Double,
                      val userid: String,
                      val userSession: String)
