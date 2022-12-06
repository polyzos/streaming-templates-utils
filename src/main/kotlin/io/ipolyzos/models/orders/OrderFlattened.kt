package io.ipolyzos.models.orders

data class OrderFlattened(val invoiceId: String,
                          val lineItemId: String,
                          val userId: String,
                          val itemId: String,
                          val itemName: String,
                          val itemCategory: String,
                          val price: Double,
                          val createdAt: Long,
                          val paidAt: Long)

