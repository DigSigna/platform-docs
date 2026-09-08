# Arquitectura de Gestión de Secretos

## Contexto
Sistema multitenant de firmas digitales utilizando SoftHSM como HSM software. Necesidad de gestionar PINs de slots HSM de forma segura en un MVP con budget controlado.

## Decisiones Arquitectónicas
1.  Almacenamiento de Secretos
    - Claves AES-256: Almacenadas exclusivamente en Kubernetes Secrets (producción) / Docker Secrets (desarrollo)
    - NUNCA en base de datos, solo metadatos
    - PINs HSM: Encriptados con AES-GCM, almacenados en DB con referencia a versión de clave
2. Derivación por Tenant
    - Clave maestra global + HKDF derivation por tenantID
    - Determinística: misma entrada = misma salida
    - Sin necesidad de almacenar claves derivadas
3. Estrategia de Rotación
    - Claves AES: Rotación periódica (90 días) o por evento
    - Llaves HSM (firma): No se rotan, se expiran y revocan
    - Documentos firmados mantienen validez con llaves originales

## Componentes Principales
### KeyManager
```go
type KeyManager interface {
    GetTenantKey(tenantID string) ([]byte, error)  // Deriva clave para tenant
    GetKey(versionID string) ([]byte, error)      // Obtiene clave por versión
    RotateKey() (string, error)                   // Genera nueva versión
    CurrentVersion() string
}
```

### SecretRepository
```go
type SecretRepository interface {
    Get(keyID string) ([]byte, error)
    Store(keyID string, data []byte) error
    Delete(keyID string) error
}
```

## Estructuras de Datos
### Slot HSM
```sql
CREATE TABLE hsm_slots (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    label VARCHAR(255) NOT NULL,
    slot_number INTEGER NOT NULL,
    encrypted_pin BYTEA NOT NULL,          -- AES-GCM(PIN)
    key_version_id VARCHAR(10) NOT NULL,   -- Referencia a versión
    encryption_context JSONB,              -- Metadatos
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
CREATE INDEX idx_hsm_slots_tenant ON hsm_slots(tenant_id);
```

### PIN Key Version
```sql
CREATE TABLE key_versions (
    version_id VARCHAR(10) PRIMARY KEY,    -- "v1", "v2"
    active BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW(),
    algorithm VARCHAR(50),                 -- "aes-256-gcm"
    source VARCHAR(255),                   -- "k8s-secret/hsm-keys"
    description TEXT
);
```

## Flujos de Trabajo
1. Creación de Nuevo Slot
```text
Input: tenantID, label
1. Deriva clave AES para tenant: HKDF(master_key, tenantID)
2. Genera PIN aleatorio para slot
3. Encripta PIN: AES-GCM(PIN, derived_key)
4. Crea slot en SoftHSM con PIN
5. Guarda en DB: {encrypted_pin, key_version_id="v1"}
```

2. Rotación de Claves AES
```text
Trigger: Periódico (90 días) o evento
1. Genera nueva clave AES (v2)
2. Guarda en K8s Secret (NO en DB)
3. Actualiza metadata: v2=active, v1=inactive
4. Rollout servicio (carga ambas versiones)
5. Background job: re-encriptar slots v1→v2
6. Eliminar v1 después de período de gracia
```

3. Gestión de Llaves HSM (Firma)
```text
Alta de nueva llave:
1. Generar par RSA/ECC en slot HSM
2. Extraer pública, almacenar en DB
3. Establecer valid_until (+2 años)

Revocación:
1. Marcar status='revoked'
2. Agregar a CRL (Certificate Revocation List)
3. Firmas existentes siguen verificables
```

## Configuración por Entorno

### Kubernetes (Producción)
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: hsm-master-keys
type: Opaque
data:
  global-master-key-v1: <base64-32-bytes>
  global-master-key-v2: <base64-32-bytes>
```

### Docker Compose (Desarrollo)
```yaml
services:
  hsm-service:
    secrets:
      - master_key_v1
      - master_key_v2

secrets:
  master_key_v1:
    file: ./secrets/master_key_v1.txt
  master_key_v2:
    file: ./secrets/master_key_v2.txt
```

## Consideraciones de Seguridad
### Principios
1. Defensa en profundidad: Claves maestras en Secret Manager, derivadas por tenant
2. Principio de mínimo privilegio: Cada componente accede solo a lo necesario
3. Auditoría completa: Todas las operaciones criptográficas registradas
4. Rotación periódica: Limita ventana de exposición

### A tener en cuenta
- K8s Secrets en etcd pueden no estar encriptados por defecto
- Considerar envelope encryption para mayor seguridad
- Implementar health checks para detectar problemas de claves

## Migración Futura
### Hacia Vault/AWS Secrets Manager
1. Implementar nuevo adaptador VaultSecretRepository
2. Migrar claves existentes con script de migración
3. Cambiar variable de entorno SECRET_PROVIDER=vault
4. Rollout gradual con feature flag

## Escalabilidad
- Cache de claves derivadas en memoria (con TTL)
- Rate limiting en operaciones criptográficas
- Monitoreo de performance de operaciones HSM

Nota: Esta arquitectura balancea seguridad y pragmatismo para MVP, permitiendo evolución futura sin deuda técnica paralizante.