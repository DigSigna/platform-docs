## DLT-007: Auditoría y No Repudio

## Metadata
- **Autor:** 
- **Fecha:** 24-10-2025
- **Estado:** REVIEW
- **Revisores:** 

**Contexto:** NOM-151 requiere auditoría inmutable de todas las operaciones de firma con no repudio. Necesitamos trazabilidad completa.

**Decisión:** Event-sourcing con Kafka + Elasticsearch para audit trail, complementado con hashchain inmutables.

**Alternativas Consideradas:**
- Blockchain público: Overkill y problemas de privacidad
- Database tradicional: No provee inmutabilidad

**Justificación:**
- Kafka provee durabilidad y orden de eventos
- Elasticsearch permite búsqueda y análisis en tiempo real
- Hashchain asegura inmutabilidad post-facto
- Cumple requisitos NOM-151 para retención

**Consecuencias:**
- ✅ Auditoría inmutable y verificable
- ✅ Búsqueda y reporting en tiempo real
- ❌ Complejidad de operación de múltiples sistemas
- ❌ Overhead de storage significativo