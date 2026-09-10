## DLT-013-Adopción de SoftHSMv2 sobre HSM Hardware

## Metadata
- **Autor:** Ray Torres
- **Fecha:** 10-11-2025
- **Estado:** APPROVED
- **Revisores:** N/A - proyecto individual (autorevisado)

**Contexto:** El proyecto requiere capacidades criptográficas robustas para operaciones de firma digital, inicialmente considerando HSM hardware (Thales Luna/AWS CloudHSM). Sin embargo, se identificaron los siguientes challenges:

1. **Costo inicial elevado:** HSM hardware implica inversión significativa (>$50K USD)
2. **Lead time de procurement:** 4-8 semanas para adquisición e instalación
3. **Complejidad operacional:** Mantenimiento especializado requerido
4. **Dependencia vendor lock-in:** Soluciones cloud propietarias

**Decisión:** Implementar SoftHSMv2 como solución HSM inicial, con capacidad de migración futura a HSM hardware sin cambios en la capa de aplicación.

**Alternativas Consideradas:**
1. **HSM Hardware (Thales Luna):** Máxima seguridad pero alto costo y complejidad
2. **AWS CloudHSM:** Menor control y dependencia de cloud provider
3. **HSM Software Básico:** No cumple estándares enterprise
4. **SoftHSMv2:** Balance entre seguridad, costo y flexibilidad

**Justificación:**
- Time-to-market acelerado: Implementación en días vs semanas
- Costo-efectividad: $0 licencias vs >$50K HSM hardware
- Standards compliance: Soporte PKCS#11, compatible con Bouncy Castle
- DevOps friendly: Contenerizado en Kubernetes, auto-scaling
- Migration path: Abstraction layer permite migración transparente futura

```yaml
SoftHSMv2 Stack:
  - Containers: Docker + Kubernetes StatefulSets
  - Storage: Persistent Volumes con encryption at rest
  - HA: Multi-replica con replication manual de keystores
  - Security: Network policies, mTLS, Vault integration
  - Backup: Automated keystore backup to encrypted S3

HSM Abstraction Layer:
  - API: gRPC + REST para operaciones criptográficas
  - Multi-tenant: Isolation por tenant vía partitions lógicas
  - Fallback: Circuit breaker para resiliencia
  - Metrics: Monitoring integrado con Prometheus
```

### Consecuencias:
- ✅ Reducción 80% en costos iniciales
- ✅ Implementación en 2 semanas vs 8+ semanas
- ✅ Flexibilidad para desarrollo y testing
- ✅ DevOps/CI/CD integration nativa
- ❌ Menor performance vs HSM hardware (~30% más lento)
- ❌ FIPS 140-2 Level 3 no disponible (solo Level 1)
- ❌ Requiere hardening security adicional
- ❌ Backup/DR más complejo (keystores vs hardware)


```yaml
NOM-151 Compliance:
  - ✅ Almacenamiento seguro de claves privadas
  - ✅ Operaciones criptográficas en entorno seguro
  - ✅ Control de acceso y auditoría
  - ⚠️ Requiere validación específica por auditor

eIDAS Compliance:
  - ✅ Qualified Signature Creation Device (QSCD) emulado
  - ✅ Secure cryptographic device implementation
```

### Métricas de Éxito
- Disponibilidad: 99.9% uptime SoftHSMv2
- Performance: < 100ms operaciones de firma
- Security: 0 vulnerabilidades críticas
- Compliance: Auditoría NOM-151 exitosa


```yaml
cluster_2x2:
  nodo_1: 2vCPU, 2GB RAM ($18/mes)
  nodo_2: 2vCPU, 2GB RAM ($18/mes)
  total: $36/mes (+33% vs configuración anterior)
```

```yaml
nodo_1:
  - api-gateway (256MB)
  - identification (256MB)
  - certificate-service (512MB)  # Más memoria para generación certs
  - istio-control-plane (512MB)

nodo_2:
  - hsm-service (512MB)  # Más recursos para operaciones cripto
  - signing-service (256MB)
  - verification-service (256MB) 
  - audit-service (128MB)
  - redis (256MB)

memoria_libre_por_nodo: ~500MB para picos
```