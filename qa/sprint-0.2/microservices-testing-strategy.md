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
