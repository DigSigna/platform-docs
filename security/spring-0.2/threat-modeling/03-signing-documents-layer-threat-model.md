# Signing-Documents Layer - Threat Modeling

## Propósito del Servicio
Proceso de firma digital de documentos, sellado de tiempo, y preservación legal.

## Componentes Críticos
- Document Signing Engine
- Timestamp Authority (TSA)
- Document Storage
- Signature Validation

## Análisis STRIDE
| Amenaza | Componente | Impacto | Mitigación |
|---------|------------|---------|------------|
| **Spoofing** | Signing Identity | Crítico | Strong authentication, biometric |
| **Tampering** | Document Pre/Post-sign | Crítico | Hash verification, WORM storage |
| **Repudiation** | Signing Process | Alto | Non-repudiation evidence |
| **Info Disclosure**| Document Content | Alto | Encryption, access controls |
| **DoS** | Signing Service | Medio | Queue system, load balancing |
| **Elevation of Privilege**| Bulk Signing | Alto | Authorization checks, quotas |

## Risk Assessment
| Riesgo | Severidad | Probabilidad | Sprint Mitigación |
|--------|-----------|--------------|-------------------|
| Altered Document | Crítico | Media | Sprint 2: Hash chain verification |
| Replay Attack | Alto | Baja | Sprint 1: Nonce implementation |
| Mass Signing Abuse | Alto | Media | Sprint 2: Signing quotas |