## DLT-003: Gestión de Identidad y Acceso Multi-Tenant

## Metadata
- **Autor:** Ray Torres 
- **Fecha:** 24-10-2025
- **Estado:** REVIEW
- **Revisores:** 

**Contexto:** Cada tenant (dependencia gubernamental) tiene sus propios usuarios, roles y posiblemente federación con sus directorios Active Directory existentes.

**Decisión:** Implementar Keycloak Multi-Realm con realm por tenant para aislamiento máximo, permitiendo federación con IDPs específicos.

**Alternativas Consideradas:**
- Single Realm con atributos tenant: Más simple pero menos aislamiento
- Auth0/Azure AD B2C: Costo operativo alto y menos control

**Justificación:**
- Aislamiento de configuración de seguridad por tenant
- Soporte nativo para SAML/OIDC/OAuth2
- Permite personalización de login pages por tenant
- Open-source y compatible con FIPS 140-2

**Consecuencias:**
- ✅ Federación flexible por tenant
- ✅ Branding personalizable
- ❌ Mayor overhead de memoria
- ❌ Complejidad operacional