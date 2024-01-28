package com.example.zeal_backend2024

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Service
import java.util.*

@Service
class TransactionService(val db: JdbcTemplate) {
    fun findTransactions(tenantId: String, customerId: String): List<Transaction> = db.query("select * from transactions where tenantId = ? AND customerId = ?", tenantId, customerId) { response, _ ->
        Transaction(
            response.getString("id"),
            response.getInt("amount"),
            response.getBoolean("void"),
            response.getString("tenantId"),
            response.getString("customerId"),
        )
    }

    fun findTransaction(tenant_id: String, id: String): List<Transaction> = db.query("select * from transactions where tenant_id = ? AND id = ?", tenant_id, id) { response, _ ->
        Transaction(
            response.getString("id"),
            response.getInt("amount"),
            response.getBoolean("void"),
            response.getString("tenantId"),
            response.getString("customerId"),
        )
    }

    fun save(transaction: Transaction): Boolean {
        val id = transaction.id ?: UUID.randomUUID().toString()
        return db.update(
            "insert into transactions values ( ?, ?, ?, ?, ? )",
            id, transaction.amount, false, transaction.tenantId, transaction.customerId
        ) > 0
    }
    fun void(tenantId: String, customerId: String, id: String): Boolean {
        return db.update(
            "update transactions set void = ? where tenantId = ? AND customerId = ? AND id = ?",
            true, tenantId, customerId, id,
        ) > 0
    }
}