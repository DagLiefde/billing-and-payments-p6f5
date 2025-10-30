-- Create schema
CREATE SCHEMA IF NOT EXISTS invoice;
SET search_path TO invoice;

CREATE TABLE IF NOT EXISTS invoices (
    invoice_id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    invoice_date TIMESTAMPTZ NOT NULL,
    due_date TIMESTAMPTZ NULL,
    status VARCHAR(16) NOT NULL,
    total_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    fiscal_folio VARCHAR(128) UNIQUE,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NULL
);

CREATE TABLE IF NOT EXISTS invoice_items (
    invoice_item_id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL REFERENCES invoices(invoice_id) ON DELETE CASCADE,
    shipment_id BIGINT NULL,
    description TEXT NOT NULL,
    quantity INT NOT NULL,
    unit_price NUMERIC(19,2) NOT NULL,
    line_total NUMERIC(19,2) NOT NULL
);

CREATE INDEX IF NOT EXISTS ix_invoice_items_shipment ON invoice_items (shipment_id);

CREATE TABLE IF NOT EXISTS invoice_history (
    history_id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL REFERENCES invoices(invoice_id) ON DELETE CASCADE,
    version BIGINT NOT NULL,
    changed_by BIGINT NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL,
    change_summary TEXT,
    snapshot_json TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS idempotency_keys (
    id BIGSERIAL PRIMARY KEY,
    service_key TEXT NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL
);



