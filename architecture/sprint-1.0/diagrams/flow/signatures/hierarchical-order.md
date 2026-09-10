# Jerarquía de Claves y Certificados - APPROACH CORRECTO

```text
Tenant (HSM Key Master)
    ↓ Firma certificados
Departamento A (HSM Key)
    ↓ Firma CSRs
Usuario 1 (Certificado X.509) → Firma documentos
```
# Flujo Completo de Firma Digital con Certificados
```text
DOCUMENTO
    ↓
HASH = SHA256(documento)
    ↓
FIRMA = RSA_Encrypt(HASH, CLAVE_PRIVADA_usuario)  ← Esto hace HSM
    ↓
PAQUETE_FIRMA = {
    "firma": FIRMA,
    "certificado": CERTIFICADO_USUARIO,  ← Solo para verificación
    "timestamp": ...
}
```