# DLT-017: Protocolo de Comunicación entre Microservicios - HTTP REST vs gRPC

## Metadata
- **Autor:** Ray Torres
- **Fecha:** 13-01-2026 
- **Estado:** APPROVED
- **Revisores:**

**Introducción:**
Este documento define la decisión sobre el protocolo de comunicación para operaciones HSM entre microservicios en la arquitectura de certificación digital, específicamente entre identification-service y hsm-service. La decisión balancea simplicidad, time-to-market y preparación para escalamiento futuro dentro de las restricciones presupuestarias del proyecto.

**Contexto:**
El sistema requiere comunicación segura y eficiente para operaciones criptográficas HSM entre servicios. Actualmente identification-service (Node.js) se comunica con hsm-service (Go) mediante HTTP REST para inicialización de slots. Se evalúa si migrar a gRPC para mejorar performance ante crecimientos futuros.

## Drivers principales:
- Presupuesto limitado a $24/mes en producción
- Frecuencia actual de operaciones baja (<10 firmas/segundo)
- Stack tecnológico heterogéneo (Node.js + Go + Python)
- Prioridad en time-to-market para MVP
- Requerimientos de seguridad enterprise-level

## Análisis de Opciones
### Opción 1: Mantener HTTP REST (RECOMENDADA)
**Ventajas:**

- [x] Simplicidad de implementación y debugging
- [x] Compatibilidad completa con stack actual (Node.js, Go, Python)
- [x] Herramientas existentes (Bruno) funcionan perfectamente
- [x] Overhead de protocolo insignificante vs tiempo HSM (5-50ms)
- [x] Time-to-market más rápido

**Desventajas:**
- Overhead ligeramente mayor que gRPC
- No óptimo para volúmenes muy altos (>1000 req/s)

### Opción 2: Migrar a gRPC
**Ventajas:**
- [x] Mejor performance para alta frecuencia (>100 req/s)
- [x] Tipado fuerte con Protocol Buffers
- [x] Streaming bidireccional nativo
- [x] Menor overhead de red

**Desventajas:**
- Complejidad aumentada en debugging
- Curva de aprendizaje para equipo
- Requiere cambios en todos los servicios
- Overengineering para volumen actual

### Opción 3: Enfoque Híbrido (Futuro)
**Características:**
- HTTP REST para operaciones de gestión (baja frecuencia)
- gRPC para operaciones críticas de firma (alta frecuencia)
- Migración gradual basada en métricas reales

## Decisión
MANTENER HTTP REST PARA TODAS LAS COMUNICACIONES INTER-SERVICIOS EN FASE MVP

## Detalles Técnicos
```yaml
protocol_decision:
  current_state: "HTTP REST con TLS"
  migration_trigger: ">100 firmas/segundo sostenidas"
  expected_timeline: "Mantener REST por 3-6 meses iniciales"

service_communication_patterns:
  hsm_service_endpoints:
    - "/internal/hsm/slots/initialize"    # HTTP REST (baja frecuencia)
    - "/api/keys/generate"                # HTTP REST (baja frecuencia) 
    - "/api/keys/sign"                    # HTTP REST (frecuencia media)
    - "/health"                           # HTTP REST (monitoring)

performance_characteristics:
  hsm_operation_latency: "5-50ms (CPU-bound en SoftHSM)"
  http_overhead: "1-5ms (insignificante comparado)"
  expected_throughput: "<100 req/segundo (capacidad actual)"
  bottleneck: "HSM, no protocolo de transporte"
```

## Justificación

**Factores Decisivos:**
1. Frecuencia Actual de Operaciones:
    ```text
        <100 firmas/segundo → HTTP REST suficiente 
        Escenario actual: <10 firmas/segundo
    ```
2. Naturaleza de Operaciones HSM:
    - Operaciones criptográficas son CPU-bound (5-50ms)
    - Overhead de HTTP (1-5ms) es insignificante comparado
    - Conclusión: El cuello de botella es el HSM, no el protocolo
3. Stack Tecnológico Heterogéneo:
    - Go (soporta ambos protocolos)
    - Node.js (soporte gRPC menos maduro)
    - Python (complejidad aumentada)
    - REST es común denominador para todos
4. Prioridades del Proyecto (MVP):
    - Time-to-market sobre optimización prematura
    - Simplicidad operacional y debugging
    - Recursos limitados ($24/mes presupuesto)

## Consecuencias
**Consecuencias Positivas**
- Desarrollo más rápido al evitar complejidad de gRPC
- Debugging más sencillo con herramientas existentes (Bruno, curl)
- Menor curva de aprendizaje para el equipo
- Fácil integración con servicios Python y Node.js existentes
- Flexibilidad para evolucionar API sin breaking changes severos

**Riesgos Asumidos**
- Performance subóptima si frecuencia excede 100 req/s prematuramente
- Necesidad de migración futura si escala significativamente
- Overhead ligeramente mayor en comunicación

**Mitigaciones**
- Monitoring estricto de métricas de performance
- Diseño de API preparado para migración futura
- Presupuesto de recursos para re-evaluación trimestral

## Plan de Evolución
### Fase 1: HTTP REST + TLS (Actual - 6 meses)
```yaml
implementation:
  protocol: "HTTP/1.1 con TLS"
  serialization: "JSON"
  authentication: "JWT tokens"
  tools: "Bruno para testing/documentación"

optimizations:
  - "HTTP persistent connections (Keep-Alive)"
  - "Compresión GZIP para payloads grandes"
  - "Implementación de circuit breakers"
  - "Rate limiting por tenant"
```

### Fase 2: HTTP/2 + Optimizaciones (6-12 meses)
```yaml
upgrades:
  - "Migrar a HTTP/2 para multiplexación"
  - "Implementar server push para notificaciones"
  - "Caché de respuestas para operaciones idempotentes"
  - "Connection pooling optimizado"

triggers:
  - ">50 req/s sostenidos"
  - "Latencia P95 > 200ms"
  - "Team size > 3 developers"
```

### Fase 3: gRPC Híbrido (Si escala)
```yaml
hybrid_approach:
  http_rest:
    - "Gestión de tenants"
    - "Administración de slots"
    - "Operaciones de baja frecuencia"
  
  grpc:
    - "SignHash (operación crítica)"
    - "VerifySignature (alta frecuencia)"
    - "Encrypt/Decrypt (si requiere baja latencia)"

migration_strategy:
  - "Coexistencia de ambos protocolos"
  - "Migración gradual por endpoint"
  - "Feature flags para rollout controlado"
```

## Métricas de Monitoreo
### Triggers para Re-evaluación
```yaml
performance_metrics:
  - "Firmas/segundo > 100 sostenido por 1 semana"
  - "Latencia P95 > 100ms atribuible a protocolo"
  - "Error rate > 0.1% por timeouts de red"
  - "Costo de infraestructura > $100/mes por overhead"

business_metrics:
  - "Crecimiento usuarios > 100% mes a mes"
  - "Enterprise client onboarding con SLA estricto"
  - "Requerimientos regulatorios nuevos"
```

## Dashboard de Monitoreo Recomendado
```text
KPI Principal:
├─ Throughput: Requests/segundo por servicio
├─ Latencia: P50, P95, P99 por endpoint
├─ Error Rate: % de errores HTTP/tiempos fuera
└─ Costo: $/operación calculado

Alertas:
├─ CRÍTICO: Error rate > 1% por 5 minutos
├─ ALTO: Latencia P95 > 500ms por 10 minutos  
├─ MEDIO: Throughput > 80% de capacidad estimada
└─ BAJO: Disponibilidad < 99.9% diario
```

## Checklist de Implementación
### Inmediato (Sprint Actual)
- Documentar endpoints REST existentes en OpenAPI/Swagger
- Implementar health checks en todos los servicios
- Configurar métricas básicas de performance
- Establecer rate limiting por tenant inicial

### Corto Plazo (1-2 meses)
- Implementar circuit breakers en clientes HTTP
- Configurar connection pooling óptimo
- Agregar tracing distribuido básico
- Establecer SLA inicial por endpoint

### Mediano Plazo (3-6 meses)
- Evaluar migración a HTTP/2
- Implementar caché para operaciones idempotentes
- Benchmark comparativo REST vs gRPC
- Plan de migración condicional a gRPC

## Conclusión
La decisión de mantener HTTP REST está fundamentada en el principio "no optimizar prematuramente". Dado el volumen actual, las características técnicas del HSM como cuello de botella, y la necesidad de time-to-market para el MVP, HTTP REST proporciona el mejor balance entre simplicidad, mantenibilidad y performance.

**Revisión programada:** Esta decisión será re-evaluada trimestralmente basada en métricas reales de uso, o inmediatamente si se disparan los triggers definidos en la sección de métricas.