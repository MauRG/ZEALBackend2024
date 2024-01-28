package com.example.zeal_backend2024

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tenant")
class TenantController(val service: TenantService, val auditLogService: AuditLogService) {
    @GetMapping
    fun index(): List<Tenant> = service.findTenants()
    @PostMapping
    fun post(@RequestBody tenant: Tenant) {
        if(service.save(tenant)) {
            auditLogService.create("Created tenant ${tenant.name} with id ${tenant.id}", "create", "userId")
        }
    }
}
