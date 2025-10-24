## DLT-006: Estrategia de Base de Datos y Persistencia

## Metadata
- **Autor:** 
- **Fecha:** 24-10-2025
- **Estado:** REVIEW
- **Revisores:** 

**Contexto:** Necesitamos base de datos transaccional robusta con capacidades criptográficas nativas, ACID y performance para operaciones de firma.

**Decisión:** PostgreSQL 15+ con extensión pgcrypto para operaciones criptográficas en base de datos.

**Alternativas Consideradas:**
- Oracle Database: Mayor costo y vendor lock-in
- Microsoft SQL Server: Menor compatibilidad cross-platform

**Justificación:**
- pgcrypto proporciona funciones criptográficas FIPS-compliant
- Schema-per-tenant nativo
- Costo-beneficio superior
- Comunidad enterprise sólida

**Consecuencias:**
- ✅ Operaciones criptográficas en DB
- ✅ Aislamiento schema-per-tenant
- ❌ Menor expertise en equipo vs Oracle
- ❌ Menor soporte 24/7 enterprise
