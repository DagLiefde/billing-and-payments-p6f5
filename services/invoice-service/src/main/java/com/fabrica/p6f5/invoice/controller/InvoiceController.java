package com.fabrica.p6f5.invoice.controller;

import com.fabrica.p6f5.invoice.dto.InvoiceDtos;
import com.fabrica.p6f5.invoice.model.Invoice;
import com.fabrica.p6f5.invoice.model.InvoiceHistory;
import com.fabrica.p6f5.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<Invoice> createDraft(@RequestHeader("X-User-Id") Long userId,
                                               @Valid @RequestBody InvoiceDtos.CreateInvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createDraft(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> get(@PathVariable Long id) {
        return ResponseEntity.ofNullable(invoiceService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateDraft(@RequestHeader("X-User-Id") Long userId,
                                               @PathVariable Long id,
                                               @Valid @RequestBody InvoiceDtos.UpdateInvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.updateDraft(userId, id, request));
    }

    @PostMapping("/{id}/issue")
    public ResponseEntity<Invoice> issue(@RequestHeader("X-User-Id") Long userId,
                                         @PathVariable Long id,
                                         @RequestBody(required = false) InvoiceDtos.IssueInvoiceRequest request) {
        String requestId = request == null ? null : request.requestId();
        return ResponseEntity.ok(invoiceService.issue(userId, id, requestId));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<InvoiceHistory>> history(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.history(id));
    }
}


