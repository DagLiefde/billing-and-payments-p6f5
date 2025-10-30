package com.fabrica.p6f5.invoice.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class InvoiceDtos {

    public record CreateInvoiceRequest(
            @NotNull Long clientId,
            @NotNull OffsetDateTime invoiceDate,
            OffsetDateTime dueDate,
            @NotEmpty List<Item> items,
            String requestId
    ) {
        public record Item(
                Long shipmentId,
                @NotBlank String description,
                @NotNull @Min(1) Integer quantity,
                @NotNull @DecimalMin("0.0") BigDecimal unitPrice
        ) {}
    }

    public record UpdateInvoiceRequest(
            @NotNull Long version,
            @NotNull OffsetDateTime invoiceDate,
            OffsetDateTime dueDate,
            @NotEmpty List<CreateInvoiceRequest.Item> items
    ) {}

    public record IssueInvoiceRequest(
            String requestId
    ) {}

    public record InvoiceResponse(
            Long id, Long clientId, OffsetDateTime invoiceDate, OffsetDateTime dueDate,
            String status, String fiscalFolio, Long version, BigDecimal totalAmount
    ) {}
}



