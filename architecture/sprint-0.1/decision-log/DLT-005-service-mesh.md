## DLT-005: Service Mesh y Comunicación entre Servicios

## Metadata
- **Autor:** Ray Torres 
- **Fecha:** 24-10-2025
- **Estado:** REVIEW
- **Revisores:** 

**Contexto:** Los microservicios requieren comunicación segura (mTLS), observabilidad y resiliencia en ambiente multi-tenant.

**Decisión:** Istio Service Mesh con mTLS obligatorio entre servicios y políticas de red por namespace de tenant.

**Alternativas Consideradas:**
- Spring Cloud Netflix stack: Menor overhead pero menos capacidades de seguridad
- Linkerd: Más ligero pero menos features enterprise

**Justificación:**
- mTLS out-of-the-box con rotación automática de certificados
- Fine-grained traffic policies por tenant namespace
- Observabilidad unificada (métricas, traces, logs)
- Compatible con Kubernetes multi-tenant

**Consecuencias:**
- ✅ Seguridad east-west robusta
- ✅ Observabilidad profunda
- ❌ Alto consumo de recursos
- ❌ Curva de aprendizaje pronunciada