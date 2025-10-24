## DLT-011: Formatos de Firma de Documentos y Estándares

## Metadata
- **Autor:** 
- **Fecha:** 24-10-2025
- **Estado:** REVIEW
- **Revisores:**

**Contexto:** Diferentes tenants requieren distintos formatos de firma electrónica según sus flujos de trabajo y normativas.

**Decisión:** Soporte nativo para PAdES (PDF), XAdES (XML), CAdES (generic) con perfiles BES/EPES según eIDAS.

**Alternativas Consideradas:**
- Solo PAdES: Limita casos de uso para tenants con sistemas XML
- Formatos propietarios: No interoperables

**Justificación:**
- Cobertura completa de casos de uso gubernamentales
- Compliance con estándares internacionales
- Interoperabilidad con otros sistemas
- Futuro-proof ante nuevos requisitos

**Consecuencias:**
- ✅ Máxima interoperabilidad
- ✅ Flexibilidad para diferentes tenants
- ❌ Complejidad de implementación múltiple formatos
- ❌ Mayor superficie de testing