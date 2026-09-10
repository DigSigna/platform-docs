```json
{
  "iss": "identification-service",
  "sub": "user-abc123",
  "aud": ["hsm-service", "signing-service", "certificate-service"],
  "exp": 1234567890,
  "iat": 1234564290,
  "jti": "token-id-12345",
  
  // Contexto de firma digital (core)
  "tenant_id": "tenant-xyz",
  "owner_type": "USER",  // o "ORGANIZATION", "SYSTEM"
  "owner_id": "user-abc123",
  "organization_id": "org-456",
  
  // Jerarquía de certificación
  "parent_key_id": "key-parent-789",
  "cert_level": 3,
  "max_cert_level": 3,
  "can_certify": false,
  
  // Ruta de certificación (para auditoría)
  "certification_path": "tenant-xyz/org-456/user-abc123",
  
  // Permisos específicos (RBAC fino)
  "permissions": {
    "keys": ["create", "read", "sign"],
    "certificates": ["issue", "revoke"],
    "max_key_size": 4096,
    "allowed_algorithms": ["RSA", "ECDSA"]
  }
}
```
 **Descripción de Campos Clave** 
- `iss` (Issuer): Servicio que emitió el token (identification-service).
- `sub` (Subject): Identificador único del usuario o entidad.
- `aud` (Audience): Servicios autorizados a consumir el token (hsm-service, signing-service, certificate-service).
- `exp` (Expiration Time): Marca temporal que indica cuándo expira el token.
- `iat` (Issued At): Marca temporal que indica cuándo se emitió el token.
- `jti` (JWT ID): Identificador único del token para evitar reutilización.
 
- `tenant_id`: Identificador del inquilino al que pertenece el usuario.
- `owner_type`: Tipo de propietario (USER, ORGANIZATION, SYSTEM).
- `owner_id`: Identificador del propietario del token.
- `organization_id`: Identificador de la organización asociada.
 
- `parent_key_id`: Identificador de la clave padre en la jerarquía de certificación.
- `cert_level`: Nivel de certificación del usuario.
- `max_cert_level`: Nivel máximo de certificación permitido.
- `can_certify`: Indica si el usuario puede emitir certificados.
 
- `certification_path`: Ruta completa de certificación para auditoría y trazabilidad.
 
- `permissions`: Objeto que define permisos específicos para operaciones criptográficas y gestión de certificados.
- `keys`: Operaciones permitidas sobre claves (create, read, sign).
- `certificates`: Operaciones permitidas sobre certificados (issue, revoke).
- `max_key_size`: Tamaño máximo permitido para claves criptográficas.
- `allowed_algorithms`: Algoritmos criptográficos permitidos (RSA, ECDSA).
 
**Servicios y Tecnologías Involucradas**

```yaml
 api-gateway:
   tecnologia: "Go + Gin"
   memoria: "15MB"
   puerto: "8080"