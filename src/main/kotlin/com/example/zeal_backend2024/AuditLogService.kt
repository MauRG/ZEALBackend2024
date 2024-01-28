package com.example.zeal_backend2024

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class AuditLogService(val db: JdbcTemplate) {
    fun findAuditLogs(): List<AuditLog> = db.query("select * from auditLogs") { response, _ ->
        AuditLog(
            response.getString("id"),
            response.getString("message"),
            response.getString("action"),
            response.getString("userId"),
            response.getString("timeStamp")
        )
    }

    fun create(message: String, action: String, userId: String) {
        val id = UUID.randomUUID().toString()
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

        db.update(
            "insert into auditLogs values ( ?, ?, ?, ?, ? )",
            id,  message, action, userId, timestamp
        )
    }
}