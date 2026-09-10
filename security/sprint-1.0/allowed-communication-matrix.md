## Flujos Autorizados
```text
API Gateway    → Todos los microservicios (8000, 8080, 3000)
Identification → Redis (6379)
Signing        → HSM Service 
All Services   → External Databases (5432, 6379)
All Services   → Audit Service (8080)
All Services   → DNS (53)
```
## Flujos Bloqueados
```text
Microservicios → Microservicios (cross-talk no autorizado)
External       → Cualquier servicio (excepto via Ingress)
Redis          → Cualquier servicio (solo ingress desde identification)