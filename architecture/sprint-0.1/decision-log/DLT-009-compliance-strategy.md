## DLT-009: Estrategia de Cumplimiento y Certificación

## Metadata
> **Superseded 10-11-2025 por [DLT-013](../../sprint-1.0/decision-log/DLT-013-softhsmv2-adoption-over-hardware-hsm.md).**
> La adopción de SoftHSMv2 abandonó la meta de certificación FIPS 140-2 desde Fase 1
> descrita abajo: solo se alcanza FIPS 140-2 Level 1 (no Level 3), y NOM-151 queda
> pendiente de validación por auditor. Fue una decisión consciente por presupuesto y
> timeline de MVP, no un descuido, el detalle de la brecha vive en
> `security/known-deviations.md`. Este documento se conserva como registro histórico
> de la ambición original.
- **Autor:** Ray Torres 
- **Fecha:** 24-10-2025
- **Estado:** SUPERSEDED by [DLT-013](../../sprint-1.0/decision-log/DLT-013-softhsmv2-adoption-over-hardware-hsm.md)
- **Revisores:** 

**Contexto:** El sistema debe cumplir múltiples normativas (NOM-151, eIDAS, FIPS 140-2) desde el inicio del desarrollo.

**Decisión:** Compliance-by-Design con auditorías continuas y certificación FIPS 140-2 desde Fase 1.

**Alternativas Consideradas:**
- Compliance posterior: Riesgo de refactoring costoso
- Certificación parcial: No cumple requisitos gubernamentales

**Justificación:**
- Evita retrabajos costosos en fases avanzadas
- Demuestra compromiso con seguridad desde inicio
- Facilita ventas a dependencias gubernamentales
- Alinea con ciclo de desarrollo seguro

**Consecuencias:**
- [x] Cumplimiento probado desde inicio
- [x] Menor riesgo de rechazo en auditorías
- [] Mayor tiempo de desarrollo inicial
- [] Costos de certificación inmediatos