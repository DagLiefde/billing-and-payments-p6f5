package com.fabrica.p6f5.invoice.service;

import com.fabrica.p6f5.invoice.dto.InvoiceDtos;
import com.fabrica.p6f5.invoice.model.*;
import com.fabrica.p6f5.invoice.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository itemRepository;
    private final InvoiceHistoryRepository historyRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final EntityManager em;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          InvoiceItemRepository itemRepository,
                          InvoiceHistoryRepository historyRepository,
                          IdempotencyKeyRepository idempotencyKeyRepository,
                          EntityManager em) {
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
        this.historyRepository = historyRepository;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.em = em;
    }

    public Invoice getById(Long id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    @Transactional
    public Invoice createDraft(Long userId, InvoiceDtos.CreateInvoiceRequest request) {
        // prevent duplicates for shipment lines
        for (var line : request.items()) {
            if (line.shipmentId() != null && itemRepository.existsByShipmentId(line.shipmentId())) {
                throw new IllegalArgumentException("Shipment already invoiced: " + line.shipmentId());
            }
        }

        Invoice invoice = new Invoice();
        invoice.setClientId(request.clientId());
        invoice.setInvoiceDate(request.invoiceDate());
        invoice.setDueDate(request.dueDate());
        invoice.setStatus(Invoice.Status.DRAFT);
        invoice.setCreatedBy(userId);
        invoice.setCreatedAt(OffsetDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (var reqItem : request.items()) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setShipmentId(reqItem.shipmentId());
            item.setDescription(reqItem.description());
            item.setQuantity(reqItem.quantity());
            item.setUnitPrice(reqItem.unitPrice());
            item.setLineTotal(reqItem.unitPrice().multiply(BigDecimal.valueOf(reqItem.quantity())));
            total = total.add(item.getLineTotal());
            invoice.getItems().add(item);
        }
        invoice.setTotalAmount(total);
        Invoice saved = invoiceRepository.save(invoice);
        saveSnapshot(saved, userId, "Created draft");
        return saved;
    }

    @Transactional
    public Invoice updateDraft(Long userId, Long invoiceId, InvoiceDtos.UpdateInvoiceRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        if (invoice.getStatus() != Invoice.Status.DRAFT) {
            throw new IllegalStateException("Only drafts can be edited");
        }
        if (!invoice.getVersion().equals(request.version())) {
            throw new IllegalStateException("Version conflict");
        }
        invoice.setInvoiceDate(request.invoiceDate());
        invoice.setDueDate(request.dueDate());

        invoice.getItems().clear();
        BigDecimal total = BigDecimal.ZERO;
        for (var reqItem : request.items()) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setShipmentId(reqItem.shipmentId());
            item.setDescription(reqItem.description());
            item.setQuantity(reqItem.quantity());
            item.setUnitPrice(reqItem.unitPrice());
            item.setLineTotal(reqItem.unitPrice().multiply(BigDecimal.valueOf(reqItem.quantity())));
            total = total.add(item.getLineTotal());
            invoice.getItems().add(item);
        }
        invoice.setTotalAmount(total);
        invoice.setUpdatedBy(userId);
        invoice.setUpdatedAt(OffsetDateTime.now());
        Invoice saved = invoiceRepository.save(invoice);
        saveSnapshot(saved, userId, "Edited draft");
        return saved;
    }

    @Transactional
    public Invoice issue(Long userId, Long invoiceId, String requestId) {
        String key = "ISSUE:" + invoiceId + ":" + (requestId == null ? UUID.randomUUID() : requestId);
        if (idempotencyKeyRepository.findByServiceKey(key).isPresent()) {
            return invoiceRepository.findById(invoiceId).orElseThrow();
        }
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        if (invoice.getStatus() != Invoice.Status.DRAFT) {
            return invoice; // idempotent behavior
        }
        validateFiscalData(invoice);
        String folio = generateFiscalFolio(invoiceId);
        invoice.setFiscalFolio(folio);
        invoice.setStatus(Invoice.Status.ISSUED);
        invoice.setUpdatedBy(userId);
        invoice.setUpdatedAt(OffsetDateTime.now());
        Invoice saved = invoiceRepository.save(invoice);

        IdempotencyKey k = new IdempotencyKey();
        k.setServiceKey(key);
        k.setCreatedAt(OffsetDateTime.now());
        idempotencyKeyRepository.save(k);

        saveSnapshot(saved, userId, "Issued invoice folio=" + folio);
        return saved;
    }

    public List<InvoiceHistory> history(Long invoiceId) {
        return historyRepository.findByInvoiceIdOrderByVersionDesc(invoiceId);
    }

    private void saveSnapshot(Invoice invoice, Long userId, String summary) {
        InvoiceHistory h = new InvoiceHistory();
        h.setInvoiceId(invoice.getId());
        h.setVersion(invoice.getVersion());
        h.setChangedBy(userId);
        h.setChangedAt(OffsetDateTime.now());
        h.setChangeSummary(summary);
        h.setSnapshotJson("{}" ); // TODO: serialize invoice to JSON
        historyRepository.save(h);
    }

    private void validateFiscalData(Invoice invoice) {
        if (invoice.getClientId() == null || invoice.getTotalAmount() == null) {
            throw new IllegalStateException("Missing fiscal data");
        }
    }

    private String generateFiscalFolio(Long invoiceId) {
        return "FISC-" + OffsetDateTime.now().toEpochSecond() + "-" + invoiceId;
    }
}


