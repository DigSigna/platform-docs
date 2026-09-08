# SUBSISTEMAS CRÍTICOS

## A. Gestión de Claves HSM (Hardware Security Module)
```text
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Tenant A   │     │  Tenant B   │     │  Tenant N   │
│  Partition  │     │  Partition  │     │  Partition  │
└──────┬──────┘     └──────┬──────┘     └──────┬──────┘
       │                   │                   │
       └───────────────────┼───────────────────┘
                           │
                    ┌──────▼──────┐
                    │   HSM Físico│
                    │  o Cloud HSM│
                    └─────────────┘
```

## B. Flujo de Validación de Certificados
```text
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Verificación│     │  Consulta   │     │  Validación │
│  Fechas      ├────►│  CRL        ├────►│  Cadena     │
└─────────────┘     └──────┬──────┘     └─────────────┘
                           │
                    ┌──────▼──────┐
                    │  Servicio   │
                    │   OCSP      │
                    └─────────────┘
```