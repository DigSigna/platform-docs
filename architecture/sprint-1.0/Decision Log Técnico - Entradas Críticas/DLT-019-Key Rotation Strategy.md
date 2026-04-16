#  DLT-010: Key Rotation Strategy

## Metadata
- **Autor:**
- **Fecha:** 15-01-2026 
- **Estado:** APPROVED
- **Revisores:**

**Introducción:**
Este documento detalla la estrategia utilizada para la rotación de claves criptográficas en nuestro sistema, asegurando la seguridad y disponibilidad continua de los servicios. Basado en las diferentes etapas del sistema.

**Contexto:**
DigSigna Platform requiere un sistema de rotación de claves AES que se adapte a de manera optima a las necesidades de seguridad y operativas del entorno multitenant.

## Tabla comparativa de Estrategias de Rotación

|Enfoque|Cuándo Usar|Pros/Cons|Ejemplo Empresa|
|-------|-----------|---------|---------------
|Manual|Desarrollo, MVP|Simple, rápido|Error-prone, no escala|Startup temprana|
|CI/CD|Producción pequeña-mediana|Auditado, versionado|Requiere infra CI/CD|Mid-market SaaS|
|Auto-rotación|Enterprise, Fintech|Zero-touch, compliant|Complejo, costoso|Banco, Fintech|
|Gradual (Canary)|Mission-critical|Minimiza riesgo, testing| Más complejo|AWS, Google Cloud|

## Decisión
Por las condiciones del proyecto y la necesidad de un balance entre seguridad y pragmatismo, se opta por una estrategia manual, asistida por scripts automatizados, por el momento no es necesario implementar una solución de auto-rotación completa, ni canary deployments, el número de slots y claves es limitado y manejable.
Para un entorno de producción bien establecido, etapa 2 o 3, se recomienda evaluar la implementación de una solución de Auto-rotación y Canary Deployments para la rotación de claves, utilizando herramientas como HashiCorp Vault o AWS Secrets Manager. Bajo analisis de condiciones se podría optar por CI/CD si la infraestructura lo permite.

Siempre se debe considerar la implementación de un grace period para las claves antiguas, permitiendo un rollback seguro en caso de problemas post-rotación.

## Key Rotation Checklist

### [ ] Pre-Rotación
- [ ] Backup completo de BD y secrets
- [ ] Notificar equipos (SRE, Dev, Security)
- [ ] Verificar health checks
- [ ] Establecer ventana de mantenimiento

### [ ] Durante Rotación
- [ ] Rotación gradual (canary -> batch)
- [ ] Monitoreo de métricas
- [ ] Verificación automática
- [ ] Logging detallado

### [ ] Post-Rotación
- [ ] Validación funcional
- [ ] Smoke tests
- [ ] Limpieza de claves antiguas (después de grace period)
- [ ] Actualizar documentación
- [ ] Post-mortem si aplica

### [ ] Fallback/Rollback
- [ ] Punto de rollback definido
- [ ] Script de rollback probado
- [ ] Grace period de claves antiguas (ej: 7 días)

## Emergency Key Rotation Runbook (Manual)
1. Generate new master key
2. Create new version in aes_key_metadata
3. Re-wrap all data keys with new master key
4. Update K8s secret with new master key
5. Rolling restart of HSM service pods
6. Mark old key version as inactive