package com.fabrica.p6f5.invoice.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "invoice_history")
public class InvoiceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "changed_by", nullable = false)
    private Long changedBy;

    @Column(name = "changed_at", nullable = false)
    private OffsetDateTime changedAt;

    @Column(name = "change_summary", length = 1000)
    private String changeSummary;

    @Lob
    @Column(name = "snapshot_json", nullable = false)
    private String snapshotJson;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Long getChangedBy() { return changedBy; }
    public void setChangedBy(Long changedBy) { this.changedBy = changedBy; }
    public OffsetDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(OffsetDateTime changedAt) { this.changedAt = changedAt; }
    public String getChangeSummary() { return changeSummary; }
    public void setChangeSummary(String changeSummary) { this.changeSummary = changeSummary; }
    public String getSnapshotJson() { return snapshotJson; }
    public void setSnapshotJson(String snapshotJson) { this.snapshotJson = snapshotJson; }
}



