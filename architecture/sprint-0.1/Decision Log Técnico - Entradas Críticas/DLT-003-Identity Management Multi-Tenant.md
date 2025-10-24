## DLT-003: Identity Management Multi-Tenant
**Decisión:** Keycloak Multi-Realm vs Single Realm
**Arquitectura:**
  - Realm por tenant para máximo aislamiento
  - Realm shared para tenants pequeños (cost optimization)
**Justificación:**
  - Configuración de seguridad por tenant
  - Branding personalizado (login pages)
  - Federación con IDPs específicos por tenant