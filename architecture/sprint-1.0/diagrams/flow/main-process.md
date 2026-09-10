# Diagrama de Flujo

```text
┌─────────────────────────────────────────────────────┐
│            INICIO SISTEMA MULTITENANT               │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│  1. REGISTRO DE TENANT (Organización/Cliente)       │
│  • Solicitud de registro                            │
│  • Validación de datos                              │
│  • Creación de tenant en BD                         │
│  • Asignación de ID único                           │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│  2. CONFIGURACIÓN HSM POR TENANT                    │
│  • Generación de claves maestras                    │
│  • Configuración de partición HSM                   │
│  • Asignación de slots criptográficos               │
│  • Almacenamiento seguro de seeds                   │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│  3. REGISTRO DE USUARIOS ASOCIADOS                  │
│  • Autenticación admin tenant                       │
│  • Creación de usuario                              │
│  • Asignación de roles/permisos                     │
│  • Vinculación a tenant                             │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│  4. GENERACIÓN DE CERTIFICADO POR USUARIO           │
│  • Solicitud CSR (Certificate Signing Request)      │
│  • Validación de identidad                          │
│  • Firma CSR con clave HSM del tenant               │
│  • Emisión de certificado digital                   │
│  • Almacenamiento seguro en repositorio             │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│  5. PROCESO DE FIRMA DE DOCUMENTO                   │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
         ┌─────────────────────────────┐
         │  5.1 AUTENTICACIÓN USUARIO  │
         │  • Login multitenant        │
         │  • Validación de sesión     │
         │  • Verificación de permisos │
         └─────────────────────────────┘
                         │
                         ▼
         ┌─────────────────────────────┐
         │  5.2 CARGA DE DOCUMENTO     │
         │  • Validación de formato    │
         │  • Verificación de integridad│
         │  • Preprocesamiento         │
         └─────────────────────────────┘
                         │
                         ▼
         ┌─────────────────────────────┐
         │  5.3 CÁLCULO DE HASH        │
         │  • Aplicación de algoritmo  │
         │    criptográfico (SHA-256)  │
         │  • Generación de hash único │
         └─────────────────────────────┘
                         │
                         ▼
         ┌─────────────────────────────┐
         │  5.4 VALIDACIÓN CERTIFICADO │
         │  • Verificación de vigencia │
         │  • Consulta CRL/OCSP        │
         │  • Validación de cadena     │
         │  • Verificación de tenencia │
         └─────────────────────────────┘
                         │
                         ▼
         ┌─────────────────────────────┐
         │  5.5 FIRMA DIGITAL EN HSM   │
         │  • Envío de hash a HSM      │
         │  • Firma con clave privada  │
         │  • Generación de sello digital│
         └─────────────────────────────┘
                         │
                         ▼
         ┌─────────────────────────────┐
         │  5.6 ESTAMPADO DE TIEMPO    │
         │  • Solicitud a TSA          │
         │  • Sello temporal RFC 3161  │
         │  • Vinculación a firma      │
         └─────────────────────────────┘
                         │
                         ▼
         ┌─────────────────────────────┐
         │  5.7 GENERACIÓN DOC FIRMADO │
         │  • Inclusión de firma       │
         │  • Metadatos de auditoría   │
         │  • Empaquetado (PDF, XML, etc)│
         └─────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│  6. RETORNO AL USUARIO                              │
│  • Entrega documento firmado                        │
│  • Registro en log de auditoría                     │
│  • Notificación de éxito                            │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│  7. VERIFICACIÓN POSTERIOR DE FIRMA                 │
│  • Validación de sello temporal                     │
│  • Verificación de firma digital                    │
│  • Consulta estado certificado                      │
│  • Generación de informe de validación              │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│  8. ALMACENAMIENTO Y AUDITORÍA                      │
│  • Archivado documental                             │
│  • Registro en blockchain (opcional)                │
│  • Reportes de actividad                            │
│  • Copias de seguridad                              │
└─────────────────────────────────────────────────────┘
```
