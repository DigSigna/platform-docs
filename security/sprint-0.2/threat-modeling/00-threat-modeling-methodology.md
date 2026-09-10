# Threat Modeling Methodology
**Versión:** 1.0 | **Fecha:** 04-11-2025

## Alcance
Análisis de seguridad para los 5 servicios core de la plataforma de firmas digitales.

## 🔧 Metodología
- **Framework:** STRIDE por componente
- **Risk Assessment:** Matriz DREAD cuantitativa
- **Coverage:** Arquitectura hexagonal + flujos de datos

##  Severity Matrix
| Nivel | Impacto | Ejemplo |
|-------|---------|---------|
| Crítico | Pérdida total de confidencialidad/integridad | Compromiso de claves privadas |
| Alto | Pérdida parcial de seguridad | Fuga de documentos firmados |
| Medio | Vulnerabilidad controlable | Exposición de metadatos |
| Bajo | Mejora de seguridad | Logs insuficientes |