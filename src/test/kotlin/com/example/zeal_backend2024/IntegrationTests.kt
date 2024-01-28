package com.example.zeal_backend2024

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.*
import org.springframework.jdbc.core.JdbcTemplate


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(@Autowired val restTemplate: TestRestTemplate) {

    @Autowired
    private lateinit var customerService: CustomerService
    @Autowired
    private lateinit var tenantService: TenantService

    @Autowired
    private val jdbcTemplate: JdbcTemplate? = null

    @Test
    fun `Assert message, content and status code`() {
        // Database setup
        // Clear db
        val tablesToTruncate = listOf("transactions", "customers", "tenants", "auditLogs")
        tablesToTruncate.forEach { jdbcTemplate?.execute("TRUNCATE TABLE $it") }

        // Customers setup
        val c1: Customer = Customer("c_1", "Backend")
        val c2: Customer = Customer("c_2", "Frontend")
        customerService.save(c1)
        customerService.save(c2)
        // Tenants setup
        val t1: Tenant = Tenant("t_1", "enterprise-all-inclusive.com")
        val t2: Tenant = Tenant("t_2", "betrieb-alles-inklusive.de")
        tenantService.save(t1)
        tenantService.save(t2)

        // Create 3 transactions for customer 1, 2 for enterprise-all-inclusive.com and 1 for betrieb-alles-inklusive.de
        val transaction1 = Transaction("tr1", 40, false, "t_1", "c_1")
        val transaction2 = Transaction("tr2", -20, false, "t_1", "c_1")
        val transaction3 = Transaction("tr3", 60, false, "t_2", "c_1")
        assertThat(postTransaction(transaction1).statusCode).isEqualTo(HttpStatus.OK)
        assertThat(postTransaction(transaction2).statusCode).isEqualTo(HttpStatus.OK)
        assertThat(postTransaction(transaction3).statusCode).isEqualTo(HttpStatus.OK)

        // Get transactions from customer 1 at the enterprise-all-inclusive.com
        // Get for entity didn't want to work and kept failing during the casting step. Using a Map instead.
        var entity = restTemplate.getForEntity<List<LinkedHashMap<String, Any>>>("/tenant/${t1.id}/customer/${c1.id}")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body?.size).isEqualTo(2)
        // Void the -20 transaction
        voidTransaction(transaction2)
        // Retrieve transactions again and verify it was voided correctly
        entity = restTemplate.getForEntity<List<LinkedHashMap<String, Any>>>("/tenant/${t1.id}/customer/${c1.id}")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body?.get(1)?.get("void")).isEqualTo(true)
        // Try to void transaction with the wrong tenant
        voidTransaction(t2.id, transaction1.customerId, transaction1.id)
        entity = restTemplate.getForEntity<List<LinkedHashMap<String, Any>>>("/tenant/${t1.id}/customer/${c1.id}")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body?.get(0)?.get("void")).isEqualTo(false)
        // Try to void transaction with the wrong customer
        voidTransaction(transaction1.tenantId, c2.id, transaction1.id)
        entity = restTemplate.getForEntity<List<LinkedHashMap<String, Any>>>("/tenant/${t1.id}/customer/${c1.id}")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body?.get(0)?.get("void")).isEqualTo(false)
        // Verify that audit logs were created for all the post and put calls that were valid (4)
        entity = restTemplate.getForEntity<List<LinkedHashMap<String, Any>>>("/audit_log")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body?.size).isEqualTo(4)
    }

    fun postTransaction(transaction: Transaction?): ResponseEntity<Transaction> {
        val jsonMapper = jacksonObjectMapper()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val jsonStr = jsonMapper.writeValueAsString(transaction)
        val request = HttpEntity<String>(jsonStr, headers)
        return restTemplate.postForEntity<Transaction>("/transaction", request, Transaction::class)
    }

    fun voidTransaction(transaction: Transaction) {
        voidTransaction(transaction.tenantId, transaction.customerId, transaction.id)
    }
    fun voidTransaction(tenantId: String?, customerId: String?, transactionId: String?) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity<String>("", headers)
        restTemplate.put("/tenant/${tenantId}/void/customer/${customerId}/transaction/${transactionId}/void", request)
    }

}