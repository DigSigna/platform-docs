#  DLT-018: Arquitectura PKI Híbrida - DigSigna Platform

## Metadata
- **Autor:** Ray Torres
- **Fecha:** 15-01-2026 
- **Estado:** APPROVED
- **Revisores:**

**Introducción:**
Este documento detalla la arquitectura PKI híbrida implementada en DigSigna Platform, que soporta tanto clientes SaaS estándar (MANAGED) como revendedores white-label (INDEPENDENT). Se describen los conceptos clave, modelos de datos, casos de uso, validaciones, modelo de negocio e interoperabilidad.

**Contexto:**
DigSigna Platform ofrece servicios de certificación digital a una variedad de clientes. Para atender diferentes necesidades, se ha diseñado una arquitectura PKI híbrida que permite operar en dos modos distintos, garantizando flexibilidad, seguridad y escalabilidad.

##  Modos de Operación

1. **MANAGED Mode** - Intermediate CA bajo DigSigna Root (SaaS estándar)
2. **INDEPENDENT Mode** - Root CA propia (White-label para revendedores)

---

##  Arquitectura General

```
┌─────────────────────────────────────────────────────────────┐
│                   DigSigna Platform                          │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │       DigSigna Root CA (cert_level=0, slot=0)        │  │
│  │       Tenant: Platform (mode=MANAGED)                │  │
│  └─────────────────┬────────────────────────────────────┘  │
│                    │                                         │
│  ┌─────────────────┴─────────────────┐                     │
│  │                                   │                     │
│  ▼ MANAGED Clients                  ▼ INDEPENDENT Clients  │
│  (Intermediate CA)                  (Root CA propia)       │
│                                                              │
│  ┌──────────────────┐               ┌──────────────────┐  │
│  │ Celaya           │               │ GobTech Platform  │  │
│  │ cert_level=1     │               │ cert_level=0      │  │
│  │ slot=0 (shared)  │               │ slot=1 (dedicated)│  │
│  └────────┬─────────┘               └────────┬─────────┘  │
│           │                                  │             │
│           ├─> Desarrollo Urbano              ├─> León      │
│           │   (cert_level=2)                 │   (cert_level=1)│
│           │                                  │             │
│           └─> Hacienda                       └─> Irapuato  │
│               (cert_level=2)                     (cert_level=1)│
│                                                              │
│  ┌──────────────────┐                                       │
│  │ ABC Corporativo  │                                       │
│  │ cert_level=1     │                                       │
│  │ slot=0 (shared)  │                                       │
│  └────────┬─────────┘                                       │
│           │                                                 │
│           └─> Sucursal Norte                                │
│               (cert_level=2)                                │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Conceptos Clave

### **1. Tenant Modes**

| Mode | Descripción | HSM Slot | Root CA | Caso de Uso |
|------|-------------|----------|---------|-------------|
| **MANAGED** | Intermediate CA bajo DigSigna Root | 0 (compartido) | DigSigna Platform | Clientes SaaS: municipios, empresas, colegios |
| **INDEPENDENT** | Root CA propia y autónoma | > 0 (dedicado) | Cliente propio | Revendedores white-label, gobiernos estatales |

### **2. HSM Slot Assignment**

```sql
-- Slot 0: Platform Root + todos los MANAGED clients
hsm_slot = 0  -- Compartido por múltiples tenants MANAGED

-- Slot 1+: INDEPENDENT clients
hsm_slot = 1  -- GobTech Platform
hsm_slot = 2  -- Gobierno del Estado
hsm_slot = 3  -- Corporativo Mega S.A.
```

### **3. Certificate Hierarchy Levels**

```
cert_level=0  Root CA (solo INDEPENDENT o Platform)
cert_level=1  Intermediate CA (MANAGED orgs) o Sub-CA (INDEPENDENT)
cert_level=2  Sub-Intermediate CA o End-entity CA
cert_level=3  End-entity certificates (usuarios)
```

---

## Modelo de Datos

### **Tabla: tenants**

```sql
CREATE TABLE tenants (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255),
    
    -- HÍBRIDO: Modo de operación
    mode ENUM('MANAGED', 'INDEPENDENT') DEFAULT 'MANAGED',
    
    -- HSM Slot
    -- MANAGED: slot=0 (compartido)
    -- INDEPENDENT: slot>0 (dedicado)
    hsm_slot INT NOT NULL,
    
    -- Relación con platform tenant (solo INDEPENDENT)
    parent_tenant_id CHAR(36),  -- NULL para platform, valor para white-label
    
    plan_type ENUM('free', 'basic', 'professional', 'enterprise', 'white_label'),
    status ENUM('active', 'suspended', 'pending', 'inactive'),
    
    FOREIGN KEY (parent_tenant_id) REFERENCES tenants(id) ON DELETE RESTRICT
);
```

### **Validaciones Críticas (Triggers)**

#### **Trigger: Validar modo híbrido en crypto_keys**

```sql
-- Al insertar clave:
IF tenant.mode = 'MANAGED' THEN
    -- Debe ser Intermediate CA bajo Platform Root
    REQUIRE parent_key_id = platform_root_key_id
    REQUIRE hsm_slot = 0
    REQUIRE cert_level >= 1
    
ELSIF tenant.mode = 'INDEPENDENT' THEN
    -- Puede ser Root CA propia
    ALLOW parent_key_id = NULL
    REQUIRE hsm_slot > 0
    REQUIRE cert_level = 0 (si root)
END IF
```

---

## Casos de Uso

### **Caso 1: Cliente SaaS Estándar (MANAGED)**

**Ejemplo:** Municipio de Celaya

```sql
-- 1. Crear tenant MANAGED
INSERT INTO tenants (name, mode, hsm_slot, parent_tenant_id, plan_type)
VALUES ('Celaya', 'MANAGED', 0, 'platform-tenant-id', 'professional');

-- 2. Crear organización principal
INSERT INTO organizations (tenant_id, type, name, level, plan_type)
VALUES ('celaya-tenant-id', 'MUNICIPALITY', 'Celaya', 0, 'professional');

-- 3. Crear Intermediate CA (bajo DigSigna Root)
INSERT INTO crypto_keys (
    tenant_id, owner_type, owner_id, 
    cert_level, parent_key_id, hsm_slot
) VALUES (
    'celaya-tenant-id', 'ORGANIZATION', 'celaya-org-id',
    1, 'digsigna-root-key-id', 0  -- parent_key_id apunta a Platform Root
);

-- 4. Emitir certificado (firmado por DigSigna Root)
INSERT INTO certificates (
    tenant_id, key_id, owner_type, owner_id,
    is_ca, path_length, cert_level,
    issuer_certificate_id, issuer_key_id,
    common_name
) VALUES (
    'celaya-tenant-id', 'celaya-key-id', 'ORGANIZATION', 'celaya-org-id',
    TRUE, 2, 1,
    'digsigna-root-cert-id', 'digsigna-root-key-id',
    'Municipio de Celaya Intermediate CA'
);
```

**Cadena de certificación resultante:**
```
DigSigna Platform Root CA (cert_level=0)
  └─> Municipio de Celaya Intermediate CA (cert_level=1)
      └─> Desarrollo Urbano CA (cert_level=2)
          └─> User Certificate (cert_level=3)
```

**Beneficios:**
- [x] Interoperable con otros clientes DigSigna
- [x] Auditoría centralizada
- [x] Control de DigSigna sobre CA
- [x] Bajo costo (HSM slot compartido)

---

### **Caso 2: Revendedor White-Label (INDEPENDENT)**

**Ejemplo:** GobTech Platform

```sql
-- 1. Crear tenant INDEPENDENT con slot dedicado
INSERT INTO tenants (name, mode, hsm_slot, parent_tenant_id, plan_type)
VALUES ('GobTech', 'INDEPENDENT', 1, 'platform-tenant-id', 'white_label');

-- 2. Crear organización raíz
INSERT INTO organizations (tenant_id, type, name, level, plan_type)
VALUES ('gobtech-tenant-id', 'RESELLER', 'GobTech Platform', 0, 'white_label');

-- 3. Crear Root CA propia (sin parent)
INSERT INTO crypto_keys (
    tenant_id, owner_type, owner_id,
    cert_level, parent_key_id, hsm_slot
) VALUES (
    'gobtech-tenant-id', 'TENANT', 'gobtech-tenant-id',
    0, NULL, 1  -- parent_key_id = NULL (Root CA independiente)
);

-- 4. Emitir certificado root auto-firmado
INSERT INTO certificates (
    tenant_id, key_id, owner_type, owner_id,
    is_ca, path_length, cert_level,
    issuer_certificate_id, issuer_key_id,
    common_name
) VALUES (
    'gobtech-tenant-id', 'gobtech-key-id', 'TENANT', 'gobtech-tenant-id',
    TRUE, 3, 0,
    NULL, NULL,  -- Auto-firmado
    'GobTech Platform Root CA'
);

-- 5. Cliente de GobTech (bajo su Root CA)
INSERT INTO organizations (
    tenant_id, parent_id, type, name, level
) VALUES (
    'gobtech-tenant-id', 'gobtech-org-id', 'MUNICIPALITY', 'León', 1
);

INSERT INTO crypto_keys (
    tenant_id, owner_type, owner_id,
    cert_level, parent_key_id, hsm_slot
) VALUES (
    'gobtech-tenant-id', 'ORGANIZATION', 'leon-org-id',
    1, 'gobtech-root-key-id', 1  -- Bajo GobTech Root
);
```

**Cadena de certificación resultante:**
```
GobTech Platform Root CA (cert_level=0, slot=1)
  └─> León Intermediate CA (cert_level=1)
      └─> Catastro CA (cert_level=2)
          └─> User Certificate (cert_level=3)
```

**Beneficios:**
- [x] White-label completo (certificados emitidos por "GobTech")
- [x] Independencia total de DigSigna
- [x] Control completo sobre sus clientes
- [ ] Sin interoperabilidad con clientes DigSigna MANAGED
- [ ] Costo adicional (HSM slot dedicado)

---

##  Validaciones y Restricciones

### **Validaciones a nivel de base de datos (Triggers)**

#### **1. Validación de modo MANAGED**
```sql
-- No puede crear Root CA propia
IF tenant.mode = 'MANAGED' AND cert_level = 0 THEN
    RAISE ERROR 'MANAGED tenants must use Intermediate CA under Platform Root'
END IF

-- Debe usar hsm_slot=0
IF tenant.mode = 'MANAGED' AND hsm_slot != 0 THEN
    RAISE ERROR 'MANAGED tenants must use shared hsm_slot=0'
END IF

-- Debe tener parent_key_id = Platform Root
IF tenant.mode = 'MANAGED' AND cert_level = 1 AND parent_key_id != platform_root THEN
    RAISE ERROR 'MANAGED tenant CA must be signed by Platform Root'
END IF
```

#### **2. Validación de modo INDEPENDENT**
```sql
-- Puede crear Root CA propia
IF tenant.mode = 'INDEPENDENT' AND cert_level = 0 THEN
    ALLOW parent_key_id = NULL
END IF

-- Debe usar hsm_slot dedicado (> 0)
IF tenant.mode = 'INDEPENDENT' AND hsm_slot = 0 THEN
    RAISE ERROR 'INDEPENDENT tenants must use dedicated hsm_slot > 0'
END IF
```

### **Validaciones a nivel de aplicación (Backend)**

```python
def create_tenant_ca(tenant_id: str, org_id: str):
    tenant = get_tenant(tenant_id)
    
    if tenant.mode == 'MANAGED':
        # Intermediate CA bajo Platform Root
        platform_root = get_platform_root_ca()
        
        return create_intermediate_ca(
            tenant_id=tenant_id,
            org_id=org_id,
            parent_key_id=platform_root.key_id,
            cert_level=1,
            hsm_slot=0,
            issuer_cert=platform_root.cert_id
        )
    
    elif tenant.mode == 'INDEPENDENT':
        # Root CA propia
        validate_hsm_slot_available(tenant.hsm_slot)
        
        return create_root_ca(
            tenant_id=tenant_id,
            org_id=org_id,
            cert_level=0,
            hsm_slot=tenant.hsm_slot,
            self_signed=True
        )
```

---

##  Modelo de Negocio

### **MANAGED Mode (SaaS estándar)**

| Plan | Precio/mes | HSM Slot | Usuarios | Firmas/mes | Interoperabilidad |
|------|-----------|----------|----------|------------|-------------------|
| Free | $0 | 0 (shared) | 5 | 100 | [X] |
| Basic | $99 | 0 (shared) | 25 | 1,000 | [X] |
| Professional | $299 | 0 (shared) | 100 | 10,000 | [X] |
| Enterprise | $999 | 0 (shared) | Ilimitado | Ilimitado | [X] |

**Facturación:** Por usuario activo + consumo de firmas

---

### **INDEPENDENT Mode (White-label)**

| Plan | Precio/mes | HSM Slot | Setup Fee | SLA |
|------|-----------|----------|-----------|-----|
| White-label | $2,999 | Dedicado (slot>0) | $5,000 | 99.9% |
| Custom | Cotización | Múltiples slots | Variable | 99.99% |

**Facturación:** 
- Mensualidad fija por slot HSM
- Setup fee inicial
- Soporte técnico dedicado
- Sin límite de usuarios/firmas

---

##  Interoperabilidad y Cross-Signing

### **Opción 1: Trust Anchors Múltiples (Recomendado para INDEPENDENT)**

```sql
-- Tabla de confianza cruzada
CREATE TABLE trusted_root_cas (
    id CHAR(36) PRIMARY KEY,
    tenant_id CHAR(36) NOT NULL,
    
    -- Root CA confiable
    trusted_cert_id CHAR(36) NOT NULL,
    trusted_cert_pem TEXT NOT NULL,
    trusted_cert_fingerprint VARCHAR(64) NOT NULL,
    
    -- Nivel de confianza
    trust_level ENUM('FULL', 'PARTIAL', 'REVOKED') DEFAULT 'FULL',
    trust_purpose VARCHAR(255),  -- 'document_signing', 'authentication', etc.
    
    added_by CHAR(36),
    added_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    expires_at TIMESTAMP(6),
    
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);

-- GobTech confía en DigSigna Root (y viceversa)
INSERT INTO trusted_root_cas (tenant_id, trusted_cert_id, trust_level)
VALUES 
    ('gobtech-tenant-id', 'digsigna-root-cert-id', 'FULL'),
    ('platform-tenant-id', 'gobtech-root-cert-id', 'FULL');
```

**Resultado:** Certificados de León (bajo GobTech) son verificables por clientes de DigSigna MANAGED

---

### **Opción 2: Cross-Signing**

```sql
-- DigSigna firma el Root CA de GobTech (sin reemplazarlo)
INSERT INTO certificates (
    tenant_id, key_id, owner_type, owner_id,
    is_ca, cert_level, path_length,
    issuer_certificate_id, issuer_key_id,
    common_name
) VALUES (
    'platform-tenant-id', 'gobtech-key-id', 'TENANT', 'gobtech-tenant-id',
    TRUE, 1, 2,
    'digsigna-root-cert-id', 'digsigna-root-key-id',
    'GobTech Platform Root CA (Cross-signed by DigSigna)'
);
```

**Resultado:** GobTech tiene 2 certificados para la misma clave:
1. Auto-firmado (para independencia)
2. Firmado por DigSigna (para interoperabilidad)

---

## Queries Útiles

### **1. Listar tenants por modo**

```sql
SELECT 
    t.name,
    t.mode,
    t.hsm_slot,
    t.plan_type,
    COUNT(DISTINCT o.id) AS total_organizations,
    COUNT(DISTINCT u.id) AS total_users,
    COUNT(DISTINCT ck.id) AS total_keys
FROM tenants t
LEFT JOIN organizations o ON o.tenant_id = t.id
LEFT JOIN users u ON u.tenant_id = t.id
LEFT JOIN crypto_keys ck ON ck.tenant_id = t.id
WHERE t.status = 'active'
GROUP BY t.id, t.name, t.mode, t.hsm_slot, t.plan_type
ORDER BY t.mode, t.hsm_slot;
```

### **2. Verificar jerarquía PKI de un tenant**

```sql
WITH RECURSIVE key_hierarchy AS (
    -- Root keys
    SELECT 
        ck.id, ck.name, ck.owner_type, ck.cert_level, 
        ck.parent_key_id, ck.hsm_slot, 0 AS depth,
        CAST(ck.name AS CHAR(1000)) AS path
    FROM crypto_keys ck
    WHERE ck.tenant_id = 'celaya-tenant-id' 
      AND ck.parent_key_id IS NULL
    
    UNION ALL
    
    -- Children keys
    SELECT 
        ck.id, ck.name, ck.owner_type, ck.cert_level,
        ck.parent_key_id, ck.hsm_slot, kh.depth + 1,
        CONCAT(kh.path, ' -> ', ck.name)
    FROM crypto_keys ck
    INNER JOIN key_hierarchy kh ON ck.parent_key_id = kh.id
)
SELECT 
    CONCAT(REPEAT('  ', depth), '└─> ', name) AS hierarchy,
    owner_type,
    cert_level,
    hsm_slot,
    path
FROM key_hierarchy
ORDER BY path;
```

### **3. Calcular consumo por tenant MANAGED vs INDEPENDENT**

```sql
SELECT 
    t.mode,
    COUNT(DISTINCT t.id) AS tenant_count,
    SUM(CASE WHEN t.mode = 'MANAGED' THEN 1 ELSE 0 END) AS managed_count,
    SUM(CASE WHEN t.mode = 'INDEPENDENT' THEN 1 ELSE 0 END) AS independent_count,
    COUNT(DISTINCT ck.id) AS total_keys,
    COUNT(DISTINCT sig.id) AS total_signatures_this_month,
    SUM(DISTINCT t.hsm_slot) AS total_hsm_slots_used
FROM tenants t
LEFT JOIN crypto_keys ck ON ck.tenant_id = t.id
LEFT JOIN signing_requests sr ON sr.tenant_id = t.id
LEFT JOIN signatures sig ON sig.signing_request_id = sr.id 
    AND sig.created_at >= DATE_SUB(NOW(), INTERVAL 1 MONTH)
WHERE t.status = 'active'
GROUP BY t.mode;
```

---

## Migración de MANAGED a INDEPENDENT

### **Proceso para convertir un cliente a white-label**

```sql
-- 1. Crear nuevo tenant INDEPENDENT
INSERT INTO tenants (name, mode, hsm_slot, parent_tenant_id, plan_type)
VALUES ('Celaya White-label', 'INDEPENDENT', 2, 'platform-tenant-id', 'white_label');

-- 2. Generar Root CA propia para Celaya
INSERT INTO crypto_keys (
    tenant_id, owner_type, owner_id,
    cert_level, parent_key_id, hsm_slot, name
) VALUES (
    'celaya-wl-tenant-id', 'TENANT', 'celaya-wl-tenant-id',
    0, NULL, 2, 'Celaya Root CA'
);

-- 3. Migrar organizaciones (cambiar tenant_id)
UPDATE organizations 
SET tenant_id = 'celaya-wl-tenant-id'
WHERE tenant_id = 'celaya-old-tenant-id';

-- 4. Re-emitir certificados bajo nueva Root CA
-- (Proceso manual con validación de identidad)

-- 5. Notificar a usuarios del cambio de cadena de confianza

-- 6. Desactivar tenant MANAGED antiguo
UPDATE tenants 
SET status = 'inactive', 
    configuration = JSON_SET(configuration, '$.migrated_to', 'celaya-wl-tenant-id')
WHERE id = 'celaya-old-tenant-id';
```

---

## Referencias

- **RFC 5280:** X.509 Public Key Infrastructure Certificate
- **PKCS#11:** Cryptographic Token Interface Standard
- **NIST SP 800-57:** Key Management Guidelines
- **eIDAS:** Electronic Identification and Trust Services (EU)

---

## Seguridad y Compliance

### **Separación de HSM Slots**

| Slot | Uso | Clientes | Riesgo |
|------|-----|----------|--------|
| 0 | Platform Root + MANAGED clients | Múltiples | Compartido |
| 1+ | INDEPENDENT clients (uno por slot) | Individual | Aislado |

**Ventaja:** Compromiso de un slot INDEPENDENT no afecta a otros tenants

### **Auditoría**

```sql
-- Log de todas las operaciones críticas
INSERT INTO audit_logs (
    tenant_id, event_type, event_action, 
    resource_type, resource_id, metadata
) VALUES (
    'celaya-tenant-id', 'PKI_OPERATION', 'CREATE_ROOT_CA',
    'CRYPTO_KEY', 'celaya-key-id',
    JSON_OBJECT(
        'mode', 'INDEPENDENT',
        'hsm_slot', 2,
        'approved_by', 'admin@digsigna.com'
    )
);
```

---

## Checklist de Implementación

- [x] Tabla `tenants` con campo `mode`
- [x] Triggers de validación híbrida
- [x] Seeds con tenant Platform MANAGED
- [x] Documentación completa
- [ ] API endpoints para crear tenant MANAGED vs INDEPENDENT
- [ ] Dashboard para monitoreo de HSM slots
- [ ] Proceso de onboarding white-label
- [ ] Sistema de facturación diferenciada
- [ ] Tests de integración para ambos modos

---
