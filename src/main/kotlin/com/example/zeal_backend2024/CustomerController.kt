package com.example.zeal_backend2024

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/customer")
class CustomerController(val service: CustomerService, val auditLogService: AuditLogService) {
    @GetMapping
    fun index(): List<Customer> = service.findCustomers()
    @PostMapping
    fun post(@RequestBody customer: Customer) {
        if(service.save(customer)) {
            auditLogService.create("Created customer ${customer.name} with id ${customer.id}", "create", "userId")
        }
    }
}
