package io.ipolyzos.models.sim

data class ServerLog(val clientIp: String,
                     val clientIdentity: String,
                     val userid: String,
                     val userAgent: String,
                     val logTime: Long,
                     val requestLine: String,
                     val statusCode: String,
                     val size: Int)
