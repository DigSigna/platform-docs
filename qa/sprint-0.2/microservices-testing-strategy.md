# Estrategia de Testing - Microservicios

## Objetivo
Establecer framework de testing escalable para arquitectura hexagonal

## Pirámide de Testing Aplicada
 
```text
    ┌─────────────────┐
    │   E2E Tests     │ <-- 10% API Contract Tests
    │  (Postman/API)  │
    ├─────────────────┤
    │ Integration     │ <-- 20% @SpringBootTest + TestContainers  
    │   (TestContainers) │
    ├─────────────────┤
    │   Unit Tests    │ <-- 70% Domain Logic Puro
    │  (JUnit + Mockito) │
    └─────────────────┘
    
```

## Scope por Template
```text
**template-base** → Tests unitarios de dominio (Entity, ValueObject, DomainEvent)
**template-spring-boot** → Tests de integración + health checks
**template-security** → Tests de seguridad + flujos auth (futuro)
```

## Scope de Seguridad — hsm-service / capa criptográfica
El testing de seguridad definido arriba (OWASP ZAP + JWT tests) aplica a servicios
web-facing como identity-service, pero no cubre `hsm-service`, que es el componente
de mayor riesgo de la plataforma. Para ese servicio, en lugar de ZAP (no aplica,
no es web-facing de la misma forma), el scope mínimo es:
- `gosec` en CI (detecta uso de `math/rand` para secretos, hardcoded credentials)
- Test de concurrencia dedicado: múltiples goroutines llamando operaciones de
  firma/verificación simultáneamente contra la misma sesión PKCS#11
- Test de integración de bootstrap: crear slot → reiniciar el servicio → verificar
  que reinicializa correctamente (cubre la clase de bug de persistencia ya vista)