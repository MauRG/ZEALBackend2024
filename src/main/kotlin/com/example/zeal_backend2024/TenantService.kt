package com.example.zeal_backend2024

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Service
import java.util.*

@Service
class TenantService(val db: JdbcTemplate) {
    fun findTenants(): List<Tenant> = db.query("select * from tenants") { response, _ ->
        Tenant(response.getString("id"), response.getString("name"))
    }

    fun save(tenant: Tenant): Boolean {
        val id = tenant.id ?: UUID.randomUUID().toString()
        return db.update(
            "insert into tenants values ( ?, ? )",
            id, tenant.name
        ) > 0
    }
}