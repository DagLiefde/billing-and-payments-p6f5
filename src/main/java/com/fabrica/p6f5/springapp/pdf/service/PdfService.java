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
            // Create PDF log entry
            PdfLog pdfLog = new PdfLog();
            pdfLog.setInvoiceId(invoiceId);
            pdfLog.setStatus(PdfLog.GenerationStatus.PENDING);
            pdfLog.setGeneratedBy(generatedBy);
            pdfLog = pdfLogRepository.save(pdfLog);
            
            // TODO: Implement actual PDF generation using iText7
            // For now, we'll generate a mock URL
            String pdfUrl = generateMockPdfUrl(invoice);
            
            // Update PDF log with success
            pdfLog.setStatus(PdfLog.GenerationStatus.SUCCESS);
            pdfLog.setPdfUrl(pdfUrl);
            pdfLog.setTemplateType("STANDARD");
            pdfLogRepository.save(pdfLog);
            
            // Update invoice with PDF URL
            invoice.setPdfUrl(pdfUrl);
            invoiceRepository.save(invoice);
            
            logger.info("PDF generated successfully for invoice id: {}", invoiceId);
            
            return pdfUrl;
            
        } catch (Exception e) {
            logger.error("Error generating PDF for invoice id: {}", invoiceId, e);
            
            // Log failure
            PdfLog pdfLog = new PdfLog();
            pdfLog.setInvoiceId(invoiceId);
            pdfLog.setStatus(PdfLog.GenerationStatus.FAILED);
            pdfLog.setErrorMessage(e.getMessage());
            pdfLog.setGeneratedBy(generatedBy);
            pdfLogRepository.save(pdfLog);
            
            throw new BusinessException(String.format(Constants.PDF_GENERATION_FAILED, e.getMessage()));
        }
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

