package io.ipolyzos.models.orders

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.*

@Serializable
data class Order(val invoiceId: String,
                 val userId: String,
                 val total: Double,
                 val createdAt: Long,
                 val paidAt: Long,
                 val lineItems: List<String>)

data class OrderAggregate(val aggregateType: String, val aggregateId: String, val type: String, val payload: String)

object OutboxTable: Table("outbox") {
    val id: Column<UUID>              = uuid("id").autoGenerate()
    val aggregateType: Column<String> = varchar("aggregatetype", 20)
    val aggregateId: Column<String>   = varchar("aggregateid", 20)
    val type: Column<String>          = varchar("type", 20)
    val payload: Column<String>       = varchar("payload", 500)

    override val primaryKey: PrimaryKey = PrimaryKey(ItemTable.id, name = "PK_OUTBOX_ID")
}