package com.fabrica.p6f5.springapp.pdf.service;

import com.fabrica.p6f5.springapp.exception.BusinessException;
import com.fabrica.p6f5.springapp.exception.ResourceNotFoundException;
import com.fabrica.p6f5.springapp.invoice.model.Invoice;
import com.fabrica.p6f5.springapp.invoice.repository.InvoiceRepository;
import com.fabrica.p6f5.springapp.pdf.model.PdfLog;
import com.fabrica.p6f5.springapp.pdf.repository.PdfLogRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fabrica.p6f5.springapp.util.Constants;
import org.springframework.stereotype.Service;

/**
 * PDF Service following Single Responsibility Principle.
 * Handles PDF generation for invoices.
 */
@Service
public class PdfService {
    
    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);
    
    private final InvoiceRepository invoiceRepository;
    private final PdfLogRepository pdfLogRepository;
    
    public PdfService(InvoiceRepository invoiceRepository, PdfLogRepository pdfLogRepository) {
        this.invoiceRepository = invoiceRepository;
        this.pdfLogRepository = pdfLogRepository;
    }
    
    /**
     * Generate PDF for an invoice
     */
    @Transactional
    public String generateInvoicePDF(Long invoiceId, Long generatedBy) {
        logger.info("Generating PDF for invoice id: {}", invoiceId);
        
        Invoice invoice = findInvoiceById(invoiceId);
        validateInvoiceStatusForPdf(invoice);
        
        try {
            return generatePdfAndSave(invoice, invoiceId, generatedBy);
        } catch (Exception e) {
            handlePdfGenerationError(invoiceId, generatedBy, e);
            throw new BusinessException(String.format(Constants.PDF_GENERATION_FAILED, e.getMessage()));
        }
    }
    
    /**
     * Generate PDF and save log entry.
     */
    private String generatePdfAndSave(Invoice invoice, Long invoiceId, Long generatedBy) {
        PdfLog pdfLog = createPendingPdfLog(invoiceId, generatedBy);
        String pdfUrl = generateMockPdfUrl(invoice);
        updatePdfLogSuccess(pdfLog, pdfUrl);
        updateInvoiceWithPdfUrl(invoice, pdfUrl);
        logger.info("PDF generated successfully for invoice id: {}", invoiceId);
        return pdfUrl;
    }
    
    /**
     * Create pending PDF log entry.
     */
    private PdfLog createPendingPdfLog(Long invoiceId, Long generatedBy) {
        PdfLog pdfLog = new PdfLog();
        pdfLog.setInvoiceId(invoiceId);
        pdfLog.setStatus(PdfLog.GenerationStatus.PENDING);
        pdfLog.setGeneratedBy(generatedBy);
        return pdfLogRepository.save(pdfLog);
    }
    
    /**
     * Update PDF log with success status.
     */
    private void updatePdfLogSuccess(PdfLog pdfLog, String pdfUrl) {
        pdfLog.setStatus(PdfLog.GenerationStatus.SUCCESS);
        pdfLog.setPdfUrl(pdfUrl);
        pdfLog.setTemplateType("STANDARD");
        pdfLogRepository.save(pdfLog);
    }
    
    /**
     * Update invoice with PDF URL.
     */
    private void updateInvoiceWithPdfUrl(Invoice invoice, String pdfUrl) {
        invoice.setPdfUrl(pdfUrl);
        invoiceRepository.save(invoice);
    }
    
    /**
     * Handle PDF generation error.
     */
    private void handlePdfGenerationError(Long invoiceId, Long generatedBy, Exception e) {
        logger.error("Error generating PDF for invoice id: {}", invoiceId, e);
        PdfLog pdfLog = createFailedPdfLog(invoiceId, generatedBy, e);
        pdfLogRepository.save(pdfLog);
    }
    
    /**
     * Create failed PDF log entry.
     */
    private PdfLog createFailedPdfLog(Long invoiceId, Long generatedBy, Exception e) {
        PdfLog pdfLog = new PdfLog();
        pdfLog.setInvoiceId(invoiceId);
        pdfLog.setStatus(PdfLog.GenerationStatus.FAILED);
        pdfLog.setErrorMessage(e.getMessage());
        pdfLog.setGeneratedBy(generatedBy);
        return pdfLog;
    }
    
    /**
     * Find invoice by ID or throw exception.
     */
    private Invoice findInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.INVOICE_NOT_FOUND + invoiceId));
    }
    
    /**
     * Validate invoice status for PDF generation.
     */
    private void validateInvoiceStatusForPdf(Invoice invoice) {
        if (invoice.getStatus() != Invoice.InvoiceStatus.ISSUED) {
            throw new BusinessException(String.format(Constants.PDF_ONLY_ISSUED, invoice.getStatus()));
        }
    }
    
    /**
     * Generate a mock PDF URL for demonstration purposes
     */
    private String generateMockPdfUrl(Invoice invoice) {
        // In a real implementation, this would upload the PDF to S3 or similar storage
        return "https://example.com/pdfs/invoice-" + invoice.getId() + ".pdf";
    }
}

