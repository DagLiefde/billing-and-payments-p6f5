CREATE SCHEMA IF NOT EXISTS document;
SET search_path TO document;

CREATE TABLE IF NOT EXISTS documents (
    document_id BIGSERIAL PRIMARY KEY,
    filename TEXT NOT NULL,
    content_type TEXT NOT NULL,
    data BYTEA NOT NULL,
    uploaded_by BIGINT NOT NULL,
    uploaded_at TIMESTAMPTZ NOT NULL
);



