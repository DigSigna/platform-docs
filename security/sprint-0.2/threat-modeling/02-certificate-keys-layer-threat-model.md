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

## Nota: Revisión post-adopción de SoftHSMv2 (ver DLT-013)
Este análisis se escribió antes de la decisión de usar SoftHSMv2 en lugar de HSM
hardware. La mitigación de "Tampering | Key Storage" arriba asume acceso físico
controlado, que no aplica a un HSM software corriendo en un contenedor. El vector
adicional a considerar: alguien con acceso root/al host o al volumen persistente
del keystore tiene una superficie de ataque a las llaves que un HSM hardware con
tamper-resistance no expone. No se rehace el ejercicio STRIDE completo para esto
para el alcance de un MVP, basta con que el hardening del nodo/contenedor
(usuario no-root, políticas de red, cifrado del volumen) se trate como mitigación
equivalente a "acceso físico controlado", y quede explícito que es una mitigación
más débil que la de un HSM hardware.