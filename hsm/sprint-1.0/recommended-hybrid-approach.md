## Para MVP: Sidecar + Configuración para Migración Futura

```yaml
# hsm-service/config/config.yaml
hsm:
  mode: "sidecar"  # Cambiar a "remote" en phase 2
  sidecar:
    library_path: "/usr/lib/softhsm/libsofthsm2.so"
    token_dir: "/var/lib/softhsm/tokens"
  remote:
    endpoint: "softhsm-service:9999"
    timeout: "30s"