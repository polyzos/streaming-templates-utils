package io.ipolyzos.models.sse

import kotlinx.serialization.Serializable

/**
outbox_events
+-----------------+--------------+------+-----+---------+----------------+
| Field           | Type         | Null | Key | Default | Extra          |
+-----------------+--------------+------+-----+---------+----------------+
| event_id        | bigint(20)   | NO   | PRI | NULL    | auto_increment |
| aggregate_id    | varchar(255) | NO   |     | NULL    |                |
| aggregate_type  | varchar(255) | NO   |     | NULL    |                |
| event_timestamp | datetime(6)  | NO   |     | NULL    |                |
| event_type      | varchar(255) | NO   |     | NULL    |                |
| payload         | json         | YES  |     | NULL    |                |
+-----------------+--------------+------+-----+---------+----------------+
 */

/*
    mediawiki.page-create
    mediawiki.page-delete
    mediawiki.page-links-change
    mediawiki.page-move
    mediawiki.page-properties-change
    mediawiki.page-undelete
    mediawiki.recentchange

    mediawiki.revision-create
    mediawiki.revision-score
    mediawiki.revision-tags-change
    mediawiki.revision-visibility-change
*/



@Serializable
data class RCFeedEvent(val schema: String,
                       val meta: Meta,
                       val id: Int? = null,
                       val type: String,
                       val namespace: Int,
                       val title: String,
                       val comment: String,
                       val timestamp: Long,
                       val user: String,
                       val bot: Boolean,
                       val minor: Boolean? = null,
                       val patrolled: Boolean? =null,
                       val length: Length? = null,
                       val revision: Revision? = null,
                       val log_id: Long? = null,
                       val log_type: String? = null,
                       val log_action: String? = null,
                       val log_params: String? = null,
                       val log_action_comment: String? = null,
                       val server_url: String,
                       val server_name: String,
                       val server_script_path: String,
                       val wiki: String,
                       val parsedcomment: String)
@Serializable
class LogParams(val userid: Int? = null,
                val img_sha1: String = "",
                val img_timestamp: String = "",
                val duration: String = "", val flags: String = "", val sitewide: String = "")

@Serializable
data class Length(val old: Int? = null, val new: Int? = null)
@Serializable
data class Revision(val old: Int? = null, val new: Int? = null)
@Serializable
data class Meta(val uri: String,
                val request_id: String,
                val id: String,
                val dt: String,
                val domain: String,
                val stream: String,
                val topic: String,
                val partition: Int,
                val offset: Long)

@Serializable
data class Server(val serverUrl: String, val serverName: String, val serverScriptPath: String, val wiki: String)
@Serializable
data class LogEvent(val logId: String, val logType: String, val logAction: String, val logParams: String, val logActionComment: String)