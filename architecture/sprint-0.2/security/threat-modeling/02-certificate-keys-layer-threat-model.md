# Certificate-Keys Layer - Threat Modeling

##  Propósito del Servicio
Gestión de certificados digitales, claves criptográficas y HSM integration.

##  Componentes Críticos
- Key Storage (HSM/Software)
- Certificate Lifecycle Management
- Key Rotation Service
- CRL/OCSP Validation

##  Análisis STRIDE
| Amenaza | Componente | Impacto | Mitigación |
|---------|------------|---------|------------|
| **Spoofing** | Certificate Issuance | Crítico | MFA para emisión, validación identidad |
| **Tampering** | Key Storage | Crítico | HSM, acceso físico controlado |
| **Repudiation** | Key Usage | Alto | Audit trail criptográfico |
| **Info Disclosure**| Private Keys | Crítico | HSM, zero-knowledge proofs |
| **DoS** | Validation Services | Medio | Cache, circuit breaker |
| **Elevation of Privilege**| Admin Access | Crítico | 4-eyes principle, JIT access |

##  Risk Assessment
| Riesgo | Severidad | Probabilidad | Sprint Mitigación |
|--------|-----------|--------------|-------------------|
| Compromiso HSM | Crítico | Baja | Sprint 3: HSM hardening |
| Certificate Misissuance | Alto | Media | Sprint 2: CA controls |
| Key Theft | Crítico | Media | Sprint 1: Key encryption at rest |