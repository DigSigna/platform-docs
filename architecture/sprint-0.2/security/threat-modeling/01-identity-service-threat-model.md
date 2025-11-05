# Identity Service - Threat Modeling Document

## Objetivo
Análisis de amenazas para el template-security (Identity Service) basado en arquitectura hexagonal.

## Componentes Analizados
- **AuthenticationService**: Lógica de autenticación
- **JwtTokenProvider**: Generación/validación tokens JWT  
- **RedisTokenBlacklistService**: Gestión de tokens revocados
- **UserRepositoryPort**: Acceso a datos de usuarios

## Análisis STRIDE
| Amenaza | Componente | Impacto | Mitigación Propuesta |
|---------|------------|---------|---------------------|
| **Spoofing** | JWT HS256 | Alto | Migrar a RS256 con clave mínima 2048bits |
| **Tampering** | Token JWT | Medio | Firma digital, validación integridad |
| **Repudiation** | Auth Logs | Bajo | Audit trail, logs inmutables |
| **Info Disclosure**| Redis Blacklist | Medio | Encriptación datos en reposo |
| **DoS** | Auth Endpoints | Alto | Rate limiting, circuit breaker |
| **Elevation of Privilege**| Role Validation | Crítico | Validación server-side roles |

## Matriz de Riesgo
| Riesgo | Severidad | Probabilidad | Acción |
|--------|-----------|--------------|--------|
| JWT HS256 reversible | Crítico | Media | Sprint 1: Migración RS256 |
| No tenant isolation | Alto | Alta | Sprint 1: Implementar multi-tenant |
| Password policy weak | Medio | Baja | Sprint 1: Política contraseñas fuertes |