package com.fabrica.p6f5.document.repository;

import com.fabrica.p6f5.document.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> { }



