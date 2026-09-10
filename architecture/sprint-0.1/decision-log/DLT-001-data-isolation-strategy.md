## DLT-001: Estrategia de Aislamiento de Datos

## Metadata
- **Autor:** Ray Torres 
- **Fecha:** 24-10-2025
- **Estado:** REVIEW
- **Revisores:** 

**Contexto:** Necesitamos servir múltiples dependencias gubernamentales (estatales, municipales) con aislamiento total de datos. Cada tenant requiere segregación estricta por compliance NOM-151 y protección de datos sensibles.

**Decisión:** Implementar Schema-per-Tenant en PostgreSQL combinado con Prefix-per-Tenant en S3 y Redis.

**Alternativas Consideradas:**
  - Database-per-Tenant: Máximo aislamiento pero costo operativo prohibitivo (>100 bases)
  - Shared Schema con tenant_id: Más simple pero riesgo de data leak y complejidad en queries

**Justificación:**
  - Balance óptimo entre aislamiento y eficiencia operativa
  - Permite backup/restore individual por tenant
  - Facilita migración de tenants grandes a instancia dedicada
  - Compatible con herramientas estándar de PostgreSQL

**Justificación:**
  - ✅ Backup granular por tenant
  - ✅ Performance isolation
  - ❌ Mayor complejidad en migraciones schema-wide
  - ❌ Connection pooling menos eficiente