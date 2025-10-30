package com.fabrica.p6f5.invoice.repository;

import com.fabrica.p6f5.invoice.model.IdempotencyKey;
import com.fabrica.p6f5.invoice.model.Invoice;
import com.fabrica.p6f5.invoice.model.InvoiceHistory;
import com.fabrica.p6f5.invoice.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}

@Repository
interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByInvoiceId(Long invoiceId);
    boolean existsByShipmentId(Long shipmentId);
}

@Repository
interface InvoiceHistoryRepository extends JpaRepository<InvoiceHistory, Long> {
    List<InvoiceHistory> findByInvoiceIdOrderByVersionDesc(Long invoiceId);
}

@Repository
interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findByServiceKey(String serviceKey);
}



