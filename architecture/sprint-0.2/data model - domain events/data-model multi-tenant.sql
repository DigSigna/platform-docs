-- Schema compartido para configuración de tenants
CREATE TABLE platform_tenants (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    plan_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    configuration JSONB NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Schema por tenant: tenant_{id}
CREATE TABLE tenant_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_id VARCHAR(255), -- ID en sistema del cliente
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    metadata JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(email)
);

CREATE TABLE tenant_certificates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES tenant_users(id),
    serial_number VARCHAR(255) NOT NULL,
    subject_dn TEXT NOT NULL,
    issuer_dn TEXT NOT NULL,
    valid_from TIMESTAMPTZ NOT NULL,
    valid_to TIMESTAMPTZ NOT NULL,
    certificate_data TEXT NOT NULL, -- PEM format
    hsm_key_handle VARCHAR(255), -- Referencia al HSM
    status VARCHAR(20) DEFAULT 'ACTIVE',
    revocation_reason VARCHAR(100),
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(serial_number)
);

CREATE TABLE tenant_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_id VARCHAR(255), -- ID en sistema del cliente
    name VARCHAR(255) NOT NULL,
    description TEXT,
    s3_key VARCHAR(500) NOT NULL, -- Referencia S3
    file_size BIGINT,
    mime_type VARCHAR(100),
    hash_sha256 VARCHAR(64), -- Hash del documento
    metadata JSONB,
    status VARCHAR(20) DEFAULT 'UPLOADED',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE tenant_signing_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_id VARCHAR(255),
    profile VARCHAR(50) NOT NULL, -- BASIC, ADVANCED, QUALIFIED
    status VARCHAR(20) DEFAULT 'CREATED',
    callback_url VARCHAR(500),
    expires_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE tenant_signing_session_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID REFERENCES tenant_signing_sessions(id),
    document_id UUID REFERENCES tenant_documents(id),
    signature_field VARCHAR(255),
    status VARCHAR(20) DEFAULT 'PENDING',
    signed_document_id UUID REFERENCES tenant_documents(id),
    signed_at TIMESTAMPTZ,
    UNIQUE(session_id, document_id)
);

CREATE TABLE tenant_signing_session_signers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID REFERENCES tenant_signing_sessions(id),
    user_id UUID REFERENCES tenant_users(id),
    certificate_id UUID REFERENCES tenant_certificates(id),
    sign_order INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    signed_at TIMESTAMPTZ,
    signature_data JSONB, -- Datos de la firma aplicada
    UNIQUE(session_id, user_id)
);