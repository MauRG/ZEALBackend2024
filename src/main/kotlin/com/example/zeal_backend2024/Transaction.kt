package com.example.zeal_backend2024

data class Transaction(val id: String?, val amount: Int, val void: Boolean, val tenantId: String, val customerId: String)