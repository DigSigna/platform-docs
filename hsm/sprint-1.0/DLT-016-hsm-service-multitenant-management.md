# IMPLEMENTAR PATRÓN ORQUESTADO

## Decisión:
Para manejar múltiples tenants en el HSM service, se implementa un patrón orquestado donde el identification-service actúa como orquestador de la lógica de negocio y el hsm-service como executor técnico.

Para MVP, se utilizará SoftHSMv2 con un pool de slots predefinidos (ej. 4 slots). Cada tenant creado recibirá un slot asignado manualmente.

## Contexto:
Para manejar múltiples tenants en el HSM service, se debe incluir un patrones arquitectónicos:
1. **Patrón Orquestado**: Un servicio orquestador (identification-service) maneja la lógica de negocio y coordina las llamadas al HSM service para inicializar slots.

## identification-service:
Orchestrator de lógica de negocio
- Gestión de pool de slots
- Validaciones de negocio (límites de plan)
- Retry logic
## hsm-service:

Executor técnico
- Endpoint /internal/hsm/slots/initialize
- Gestión directa de softhsm2-util
- Registro dinámico de HSMClients
## Beneficios:

- Clara separación de responsabilidades
- Mejor modelo de seguridad
- Escalable a múltiples HSMs
- Más fácil de testear
- Permite retry logic en orquestador

```text
┌─────────────────────────────────────────────────────────────────┐
│                    FLUJO DE CREACIÓN DE TENANT                   │
└─────────────────────────────────────────────────────────────────┘

1. SuperAdmin → API Gateway → identification-service
   POST /tenants { name, email, plan }

2. identification-service (Orchestrator)
   ├─ Crear registro Tenant (DB)
   ├─ Asignar slot disponible (lógica de pool)
   └─ **Llamar a hsm-service** 
      POST /internal/hsm/slots/initialize
      { "slot": 2, "tenant_id": "uuid", "pin": "generated" }

3. hsm-service (Executor)
   ├─ Ejecutar softhsm2-util --init-token (via exec.Command)
   ├─ Crear SoftHSMClient para el nuevo slot
   ├─ Registrar en HSMManager
   └─ Retornar success/failure

4. identification-service
   └─ Actualizar Tenant.status = "active" | "failed"
```