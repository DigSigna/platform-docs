-- Script de migración: V1__Initial_MultiTenant_Schema.sql
CREATE OR REPLACE FUNCTION create_tenant_schema(tenant_id VARCHAR) 
RETURNS VOID AS $$
BEGIN
    EXECUTE format('CREATE SCHEMA IF NOT EXISTS tenant_%s', tenant_id);
    
    -- Crear tablas en el schema del tenant
    EXECUTE format('
        CREATE TABLE tenant_%s.users (
            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
            email VARCHAR(255) NOT NULL UNIQUE,
            -- ... resto de campos
            created_at TIMESTAMPTZ DEFAULT NOW()
        )', tenant_id);
        
    -- Repetir para todas las tablas del tenant
END;
$$ LANGUAGE plpgsql;

-- Procedimiento para onboarding de nuevo tenant
CREATE OR REPLACE PROCEDURE onboard_new_tenant(
    p_tenant_id VARCHAR,
    p_tenant_name VARCHAR,
    p_contact_email VARCHAR
)
AS $$
BEGIN
    -- 1. Registrar tenant en tabla maestra
    INSERT INTO platform_tenants (id, name, contact_email, plan_type, configuration)
    VALUES (p_tenant_id, p_tenant_name, p_contact_email, 'BASIC', '{}');
    
    -- 2. Crear schema específico
    PERFORM create_tenant_schema(p_tenant_id);
    
    -- 3. Configurar roles y permisos
    EXECUTE format('GRANT USAGE ON SCHEMA tenant_%s TO app_user', p_tenant_id);
    EXECUTE format('GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA tenant_%s TO app_user', p_tenant_id);
    
    -- 4. Configurar Keycloak Realm
    -- (Integración con Keycloak Admin API)
    
    -- 5. Configurar API Gateway routes
    -- (Integración con Kong/Spring Cloud Gateway)
END;
$$ LANGUAGE plpgsql;