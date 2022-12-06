package io.ipolyzos.models.orders

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

data class Item(val id: Int,
                val createdAt: Long,
                val createdAtEpoch: Long,
                val adjective: String,
                val category: String,
                val modifier: String,
                val name: String,
                val price: Double)

object ItemTable: Table("Items"){
    val id: Column<Int>              = integer("id")
    val createdAt: Column<Long>      = long("createdAt")
    val createdAtEpoch: Column<Long> = long("createdAtEpoch")
    val adjective: Column<String>    = varchar("adjective", 50)
    val category: Column<String>     = varchar("category", 50)
    val modifier: Column<String>     = varchar("modifier", 50)
    val name: Column<String>         = varchar("name", 50)
    val price: Column<Double>        = double("price")

    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "PK_Item_ID")
}