package com.example.zeal_backend2024

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomerService(val db: JdbcTemplate) {
    fun findCustomers(): List<Customer> = db.query("select * from customers") { response, _ ->
        Customer(response.getString("id"), response.getString("name"))
    }

    fun save(customer: Customer): Boolean {
        val id = customer.id ?: UUID.randomUUID().toString()
        return db.update(
            "insert into customers values ( ?, ? )",
            id, customer.name
        ) > 0
    }
}