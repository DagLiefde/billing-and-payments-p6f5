package com.fabrica.p6f5.shipment.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "shipments")
public class Shipment {
    public enum Status { CREATED, DELIVERED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Long id;

    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.CREATED;

    @Column(name = "locked", nullable = false)
    private boolean locked;

    @Column(name = "invoiced", nullable = false)
    private boolean invoiced;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
    public boolean isInvoiced() { return invoiced; }
    public void setInvoiced(boolean invoiced) { this.invoiced = invoiced; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}



