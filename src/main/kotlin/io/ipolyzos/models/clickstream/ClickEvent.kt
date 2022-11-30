package io.ipolyzos.models.clickstream

data class ClickEvent(val eventTime: Long,
                      val eventType: String,
                      val productId: String,
                      val categoryId: String,
                      val categoryCode: String,
                      val brand: String,
                      val price: String,
                      val userid: String,
                      val userSession: String)
