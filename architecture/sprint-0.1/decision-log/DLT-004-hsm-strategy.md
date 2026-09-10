## DLT-004: Estrategia HSM y Operaciones Criptográficas

## Metadata
- **Autor:** 
- **Fecha:** 24-10-2025
- **Estado:** REVIEW
- **Revisores:**

**Contexto:** Las operaciones criptográficas sensibles (generación de claves, firma) requieren certificación FIPS 140-2 Level 3 y aislamiento por tenant.

**Decisión:** Thales Luna HSM 7 con partitions lógicas por tenant + AWS CloudHSM como backup geográfico.

**Alternativas Consideradas:**
- Solo AWS CloudHSM: Menor control y dependencia de cloud provider
- HSM software: No cumple FIPS 140-2 Level 3

**Justificación:**
- Certificación FIPS 140-2 Level 3 comprobada
- Partitions lógicas permiten aislamiento criptográfico por tenant
- Modelo híbrido (on-prem + cloud) para resiliencia
- Amplia experiencia en entornos gubernamentales

**Consecuencias:**
- ✅ Compliance FIPS 140-2 Level 3
- ✅ Aislamiento criptográfico por tenant
- ❌ Alto costo inicial y mantenimiento
- ❌ Complejidad de configuración y operación