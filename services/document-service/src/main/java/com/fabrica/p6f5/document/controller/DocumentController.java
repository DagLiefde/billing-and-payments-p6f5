package com.fabrica.p6f5.document.controller;

import com.fabrica.p6f5.document.model.Document;
import com.fabrica.p6f5.document.repository.DocumentRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final DocumentRepository repository;

    public DocumentController(DocumentRepository repository) { this.repository = repository; }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> upload(@RequestHeader("X-User-Id") Long userId,
                                       @RequestParam("file") MultipartFile file) throws IOException {
        Document d = new Document();
        d.setFilename(file.getOriginalFilename());
        d.setContentType(file.getContentType());
        d.setData(file.getBytes());
        d.setUploadedBy(userId);
        d.setUploadedAt(OffsetDateTime.now());
        return ResponseEntity.ok(repository.save(d).getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        return repository.findById(id)
                .map(d -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + d.getFilename())
                        .contentType(MediaType.parseMediaType(d.getContentType()))
                        .body(d.getData()))
                .orElse(ResponseEntity.notFound().build());
    }
}



