## DLT-014: Cambio de Arquitectura

## Metadata
> **Superseded 19-11-2025 por DLT-015.** El presupuesto de producción bajó de $74/mes a $24/mes y DLT-015 agrega deuda técnica asumida y parámetros de upgrade. Este documento se conserva como registro histórico de la decisión original.
- **Autor:** Ray Torres Ray Torres
- **Fecha:** 11-11-2025
- **Estado:** SUPERSEDED by [DLT-015](./DLT-015-architecture-change.md)
- **Revisores:** N/A - proyecto individual. Revisión técnica asistida por Claude (sin revisor humano secundario)

**Introducción:**Este documento define la arquitectura para un sistema de certificación digital basado en microservicios, diseñado para operar dentro de restricciones presupuestarias específicas ($74/mes) mientras mantiene altos estándares de seguridad, escalabilidad y desempeño.

**Contexto:** El proyecto requiere construir una plataforma multi-tenant de certificación digital que incluya generación de certificados, gestión de identidades, firmas digitales y verificación. Los drivers principales son:
- Presupuesto limitado a $74/mes en producción
- Requerimientos de seguridad enterprise-level
- Escalabilidad granular para componentes específicos
- Time-to-market competitivo

### Decisión
**Patrón Arquitectónico** 
Microservicios sobre Monolito Híbrido, justificado por:

- Escalabilidad independiente por componente
- Aislamiento de fallos
- Flexibilidad tecnológica por servicio
- Migración gradual posible
- Mejor alineación con equipo distribuido

**Stack Tecnológico Poliglota**
Go + Python + Node.js seleccionado sobre stack único porque:

- Go: Máximo performance para gateway y servicios críticos (15-20MB RAM)
- Python: Desarrollo rápido para lógica compleja PKI (70-120MB RAM)
- Node.js: Eficiencia I/O para autenticación (80MB RAM)
- Redis: Cache interno optimizado (128MB) 

**Infraestructura**
3 nodos Kubernetes (2GB RAM, 2vCPU) en Digital Ocean porque:
- Alta disponibilidad con budget controlado
- Escalabilidad vertical futura
- Ecosistema container nativo
- Balance costo/performance óptimo

**InfraesServicios Gestionados Externosructura**
PostgreSQL Managed + DO Spaces para:
- Reducir overhead operacional en nodos K8s
- Backup y HA automáticos
- Enfoque en lógica de negocio vs infraestructura

### Detalles Técnicos
```yaml
api-gateway:
  tecnologia: "Go + Gin"
  memoria: "15MB"
  puerto: "8080"
  responsabilidad: "Routing, rate limiting, auth inicial"

identification-service:
  tecnologia: "Node.js + Express + JWT"
  memoria: "80MB" 
  dependencias: "Redis para tokens"
  puerto: "3000"

certificate-service:
  tecnologia: "Python + FastAPI + cryptography"
  memoria: "120MB"
  dependencias: "SoftHSMv2 via python-pkcs11"
  puerto: "8000"

hsm-service:
  tecnologia: "Go + pkcs11"
  memoria: "20MB"
  responsabilidad: "Operaciones criptográficas seguras"

signing-service:
  tecnologia: "Python + FastAPI"
  memoria: "80MB"
  puerto: "8001"

verification-service: 
  tecnologia: "Python + FastAPI"
  memoria: "80MB"
  puerto: "8002"

audit-service:
  tecnologia: "Go + Gin"
  memoria: "15MB"
  puerto: "8081"
```

**Distribución en Nodos**

***Nodo 1 (Control Plane + Core)***
- api-gateway (Go): 15MB
- identification-service (Node.js): 80MB
- redis: 128MB
- istiod: 200MB
- kube-system: 400MB

Total: 823MB + buffer

***Nodo 2 (PKI Services)***
- certificate-service (Python): 120MB
- hsm-service (Go): 20MB
- verification-service (Python): 80MB
- kube-system: 400MB

Total: 620MB + buffer

***Nodo 3 (Operational Services)***
- signing-service (Python): 80MB
- audit-service (Go): 15MB
- istio-ingress: 100MB
- kube-system: 400MB

Total: 595MB + buffer

**Especificaciones de Infraestructura**

```yaml
kubernetes_cluster:
  nodos: "3"
  especificacion: "2GB RAM, 2vCPU cada uno"
  provider: "Digital Ocean"
  costo: "$54/mes"

base_datos:
  servicio: "DO Managed PostgreSQL"
  plan: "Basic - 1GB RAM"
  costo: "$15/mes"

storage:
  servicio: "DO Spaces"
  capacidad: "250GB"
  costo: "$5/mes"

total_mensual: "$74/mes"
```

**Especificaciones de Infraestructura**

```yaml
network_policies:
  - "Default deny all"
  - "API Gateway puede comunicarse con todos los servicios"
  - "Services no pueden comunicarse entre sí directamente"
  - "Redis solo accesible por identification-service"

resource_quotas:
  memoria_total: "1.5GB por namespace"
  cpu_total: "1800m por namespace"
  pods_maximos: "20 por nodo"

soft_hsm:
  libreria: "SoftHSMv2"
  configuracion: "Token por tenant"
  backup: "DO Spaces + encriptación"