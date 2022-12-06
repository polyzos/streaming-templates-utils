package io.ipolyzos.models.orders

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

data class OrderUser(val id: Int,
                     val firstName: String,
                     val lastName: String,
                     val emailAddress: String,
                     val createdAt: Long,
                     val deletedAt: Long,
                     val mergedAt: Long,
                     val parentUserId: Int)


object UsersTable: Table("users") {
    val id: Column<Int>              = integer("invoiceId")
    val firstName: Column<String>    = varchar("firstName", 50)
    val lastName: Column<String>     = varchar("lastName", 50)
    val emailAddress: Column<String> = varchar("emailAddress", 50)
    val createdAt: Column<Long>      = long("createdAt")
    val deletedAt: Column<Long>      = long("deletedAt")
    val mergedAt: Column<Long>       = long("mergedAt")
    val parentUserId: Column<Int>    = integer("parentUserId")

    override val primaryKey: PrimaryKey = PrimaryKey(ItemTable.id, name = "PK_USER_ID")
}