package com.example.zeal_backend2024

import org.springframework.web.bind.annotation.*

@RestController
class TransactionController(val service: TransactionService, val auditLogService: AuditLogService) {
    @GetMapping("/tenant/{tenantId}/customer/{customerId}")
    fun index(@PathVariable tenantId: String,@PathVariable customerId: String): List<Transaction> = service.findTransactions(tenantId, customerId)
    @PostMapping("/transaction")
    fun post(@RequestBody transaction: Transaction) {
        if(service.save(transaction)) {
            auditLogService.create(
                "Created transaction ${transaction.id} belonging to ${transaction.customerId} on ${transaction.tenantId} with ${transaction.amount}",
                "create",
                "userId"
            )
        }
    }
    @PutMapping("/tenant/{tenantId}/void/customer/{customerId}/transaction/{id}/void")
    fun post(@PathVariable tenantId: String,@PathVariable customerId: String,@PathVariable id: String) {
        if(service.void(tenantId, customerId, id)) {
            auditLogService.create("Voided transaction $id belonging to $customerId on $tenantId", "void", "userId")
        }
    }
}
