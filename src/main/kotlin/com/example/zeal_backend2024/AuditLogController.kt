package com.example.zeal_backend2024

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/audit_log")
class AuditLogController(val service: AuditLogService) {
    @GetMapping
    fun index(): List<AuditLog> = service.findAuditLogs()
}
