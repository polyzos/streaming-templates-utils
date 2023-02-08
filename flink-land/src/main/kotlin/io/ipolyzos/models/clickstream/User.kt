package io.ipolyzos.models.clickstream

import kotlinx.serialization.Serializable

@Serializable
data class User(val userId: String,
                val firstname: String,
                val lastname: String,
                val username: String,
                val email: String,
                val title: String,
                val address: String)

