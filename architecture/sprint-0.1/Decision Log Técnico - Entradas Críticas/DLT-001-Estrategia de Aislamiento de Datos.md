## DLT-001: Estrategia de Aislamiento de Datos
**Decisión:** Schema-per-Tenant en PostgreSQL
**Alternativas Consideradas:**
  - Database-per-Tenant: Máximo aislamiento, alto costo operativo
  - Shared-Schema con tenant_id: Más simple, riesgo de data leak
**Justificación:**
  - Balance óptimo aislamiento/eficiencia
  - Backup/restore individual por tenant
  - Permite customización de esquemas futura
  - Compatible con herramientas estándar