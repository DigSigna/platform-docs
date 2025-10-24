## DLT-002: Estrategia de APIs Multi-Tenant
**Decisión:** API Gateway con Tenant Resolution multi-modal
**Modos soportados:**
  1. Header-based (X-Tenant-ID + API Key) → Primary
  2. JWT claim (tenant_id) → Para autenticación OAuth2
  3. Subdomain (tenant.platform.com) → Para web apps
**Justificación:**
  - Flexibilidad máxima para diferentes clientes
  - Compatible con sistemas legacy y modernos
  - No requiere cambios en clientes existentes