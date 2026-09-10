# Cryptographic Specification
**Sprint:** 0.2 | **Estado:** Especificación

> **Parcialmente superado por `security/sprint-1.0/secrets-management-architecture.md`.**
> La sección "Data Encryption / Secrets Management" de este documento describe un plan
> hacia HashiCorp Vault para Sprint 2 que no se ejecutó así: la decisión vigente es
> K8s Secrets + HKDF por tenant (ver DLT-013 y `secrets-management-architecture.md`),
> con Vault diferido como migración futura. Las secciones de JWT y Password Hashing
> de este documento siguen vigentes.

## JWT Token Security
### Estado Actual (Template-Security)
```java
// CURRENT - REQUIRES MIGRATION
SignatureAlgorithm.HS256
Keys.secretKeyFor(SignatureAlgorithm.HS256)
```

### Especificación Target
```yaml
JWT Signing:
  algorithm: "RS256"
  key_size: "2048 bits mínimo"
  private_key_storage: "HSM (Futuro) / Secure Key Store"
  public_key_distribution: "JWK Endpoint"

Key Rotation:
  rotation_interval: "90 días"
  key_versions: "2 activas simultáneas"
  emergency_rotation: "Procedimiento documentado"

Token Expiry:
  access_token: "15 minutos"
  refresh_token: "7 días"
```

## Password Hashing
### Estado Actual (Template-Security)
```java
// CORRECTO - NO MODIFICAR
org.springframework.security.crypto.password.PasswordEncoder
passwordEncoder.encode("password")
```
### Especificación Target
```yaml
Password Hashing:
  algorithm: "BCrypt"
  cost_factor: 12
  salt_entropy: "128 bits"
  pepper: "Opción futura con HSM"
  rotation_policy: "No aplicable (hash irreversible)"
```
## Data Encryption
### Especificación Target
```yaml
Encryption at Rest:
  algorithm: "AES-256-GCM"
  key_management: "HSM target"
  iv_generation: "Criptográficamente seguro"

Secrets Management:
  current: "Properties files"
  target: "HashiCorp Vault"
  migration_plan: "Sprint 2"
```

## Crypto Compliance
- NIST SP 800-131A: Transición a algoritmos aprobados
- FIPS 140-2: HSM requirement para entornos productivos