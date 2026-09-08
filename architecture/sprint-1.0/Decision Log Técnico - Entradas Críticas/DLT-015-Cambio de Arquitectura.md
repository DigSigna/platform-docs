## DLT-014: Cambio de Arquitectura

## Metadata
- **Autor:**
- **Fecha:** 19-11-2025
- **Estado:** APPROVED
- **Revisores:**

**Introducción:**Este documento define la arquitectura para un sistema de certificación digital basado en microservicios, diseñado para operar dentro de restricciones presupuestarias específicas ($24/mes) mientras mantiene altos estándares de seguridad, escalabilidad y desempeño.

**Contexto:** El proyecto requiere construir una plataforma multi-tenant de certificación digital que incluya generación de certificados, gestión de identidades, firmas digitales y verificación. Los drivers principales son:
- Presupuesto limitado a $24/mes en producción
- Requerimientos de seguridad enterprise-level
- Escalabilidad granular para componentes específicos
- Time-to-market competitivo

### Decisión

**Infraestructura**
1 nodos Kubernetes (4GB RAM, 2vCPU) en Digital Ocean porque:
- Alta disponibilidad con budget controlado
- Escalabilidad vertical futura
- Ecosistema container nativo
- Balance costo/performance óptimo MVP

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

**Especificaciones de Infraestructura**

```yaml
provider: "DigitalOcean"
  node_spec:
    type: "Basic Droplet"
    cpu: "2 vCPU"
    memory: "4GB RAM" 
    storage: "80GB SSD"
    network: "1000Mbps"
    
cluster_composition:
    nodes: 1
    total_capacity:
      cpu: "2000m"
      memory: "3.2GB usable"
      storage: "80GB"

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

**Tecnología Implementada**

```yaml
orchestration:
  kubernetes: "1.28+"
  cni: "flannel (default DO)"
  ingress: "nginx-ingress"
  service_mesh: "NINGUNO"

development_tools:
  local_env: "minikube/k3d"
  registry: "DOCR"
  gitops: "FluxCD (pendiente)"

observability:
  logging: "FluentBit + Loki"
  metrics: "Prometheus + Grafana"
  tracing: "NINGUNO"
```

**Distribución de Recursos**
```yaml
resource_allocation:
  system_overhead: "800MB RAM, 500m CPU"
  available_for_workloads: "2.4GB RAM, 1500m CPU"
  
  microservices:
    api-gateway: "128MB RAM, 100m CPU"
    identification-service: "192MB RAM, 150m CPU" 
    hsm-service: "384MB RAM, 300m CPU"
    certificate-service: "128MB RAM, 100m CPU"
    signing-service: "96MB RAM, 80m CPU"
    verification-service: "96MB RAM, 80m CPU"
    audit-service: "96MB RAM, 80m CPU"
    redis: "128MB RAM, 100m CPU"
    
  total_used: "~1.2GB RAM, ~900m CPU"
  headroom: "~1.2GB RAM, ~600m CPU (50%)"
```

#### Deuda Técnica y Plan de Escalamiento

***Deuda Técnica Catalogada***

```yaml
debt_high_priority:
  - service_mesh: "Falta mTLS entre servicios"
  - auto_scaling: "No HPA configurado"
  - security_hardening: "Network policies básicas"
  - backup_strategy: "Solo backups manuales"

debt_medium_priority:
  - gitops: "Deployments manuales"
  - advanced_monitoring: "Falta distributed tracing"
  - disaster_recovery: "No procedure documentado"
  - multi_zone: "Single zone deployment"

debt_low_priority:
  - cost_optimization: "No resource right-sizing"
  - performance_tuning: "Configuraciones default"
  - documentation: "Falta runbooks operativos"
```

***Plan de Escalamiento por Fases***
```yaml
phase_1_trigger: "> 500 req/día o > 50 usuarios concurrentes"
phase_1_actions:
  - agregar_segundo_nodo: "2 nodos 4GB RAM, 2vCPU"
  - implementar_hpa: "Horizontal Pod Autoscaling"
  - configurar_backups: "Velero + DO Spaces"
  - tiempo_estimado: "2-3 días"

phase_2_trigger: "> 2,000 req/día o requisitos compliance"
phase_2_actions: 
  - service_mesh: "Linkerd para mTLS y observability"
  - network_policies: "Calico para security hardening"
  - multi_zone: "Distribución 3 nodos across zones"
  - tiempo_estimado: "1-2 semanas"

phase_3_trigger: "> 10,000 req/día o enterprise clients"
phase_3_actions:
  - dedicated_hsm: "Hardware Security Module físico"
  - advanced_ci_cd: "GitOps completo + policy enforcement"
  - service_isolation: "Namespaces por tenant/ambiente"
  - tiempo_estimado: "3-4 semanas"
```

#### Características Mínimas de Seguridad

***Security Posture Actual***
```yaml
network_security:
  - firewall: "DO Cloud Firewall (puertos esenciales)"
  - ingress: "TLS termination + rate limiting básico"
  - internal_traffic: "Sin mTLS (deuda técnica)"

identity_access:
  - rbac: "ServiceAccounts por namespace"
  - secrets: "Kubernetes Secrets (encryption at rest)"
  - registry: "DOCR con scanning básico"

application_security:
  - containers: "Non-root users enforced"
  - resources: "Limits para prevenir DoS"
  - images: "Base images oficiales + updates"
```

***Security Gaps Críticos***

```yaml
high_risk_gaps:
  - "Falta mTLS entre microservicios"
  - "No network policies para isolation"
  - "Logs sin retención a largo plazo"
  - "Falta secret rotation automático"

medium_risk_gaps:
  - "No vulnerability scanning en pipeline"
  - "Falta WAF para API Gateway"
  - "Backups no automatizados"
  - "No security auditing"
```

#### Consideraciones Arquitectónicas Críticas

***Decisiones Técnicas Clave***
```yaml
architecture_decisions:
  - single_node: "Trade-off disponibilidad por simplicidad MVP"
  - no_service_mesh: "Overhead > benefit para 100 req/día"
  - external_database: "Elimina carga de data persistence"
  - managed_redis: "Redis operado por DO por consistencia"

rationale:
  - "Prioridad: Time-to-market sobre escalabilidad inicial"
  - "Security básica suficiente para etapa temprana"
  - "Costo/beneficio justifica deuda técnica controlada"
  - "Migration path claro para escalamiento futuro"
```

***Riesgos Asumidos y Mitigación***
```yaml
accepted_risks:
  - single_point_failure: "Mitigado con recovery procedures"
  - security_posture_basic: "Mitigado con isolation network"
  - manual_operations: "Mitigado con documentación clara"

contingency_plans:
  - node_failure: "Recovery desde backup en < 1 hora"
  - security_incident: "Isolate y restore desde clean state"
  - performance_degradation: "Scale up vertical inmediato"
```

#### Métricas de Success y Triggers

***Business Metrics***
```yaml
success_metrics:
  - throughput: "> 95% requests exitosas"
  - latency: "P95 < 2s para operaciones de firma"
  - availability: "> 99.5% uptime mensual"
  - user_growth: "> 10% semana a semana"

failure_conditions:
  - latency: "P95 > 5s por más de 1 hora"
  - errors: "Error rate > 1% por más de 30 minutos"
  - resources: "Memory usage > 90% por más de 15 minutos"
```

***Technical Debt Triggers***
```yaml
pay_debt_triggers:
  - "Team size > 3 developers"
  - "Daily requests > 1,000 consistentemente"
  - "Security audit requirement"
  - "Enterprise client onboarding"
  - "Funding round secured"
```

### Security Posture Aceptado

```yaml
security_baseline:
  - "Default-deny network policies"
  - "TLS termination en ingress"
  - "Non-root containers"
  - "Resource limits para prevención DoS"
  - "Service accounts básicos"

security_debt:
  - "mTLS entre servicios → Phase 2"
  - "Vulnerability scanning pipeline → Phase 2" 
  - "WAF avanzado → Phase 2"
  - "Secret rotation automático → Phase 2"
```