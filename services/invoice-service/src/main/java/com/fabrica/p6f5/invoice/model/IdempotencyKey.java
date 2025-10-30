package com.fabrica.p6f5.invoice.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "idempotency_keys", indexes = {
        @Index(name = "ix_idem_key_service", columnList = "service_key")
})
public class IdempotencyKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_key", nullable = false, unique = true)
    private String serviceKey; // e.g., ISSUE:invoiceId:requestId

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getServiceKey() { return serviceKey; }
    public void setServiceKey(String serviceKey) { this.serviceKey = serviceKey; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}



