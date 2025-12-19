# Roadmap de Transición

```yaml
phase_1_mvp:
  duration: "2-3 meses"
  architecture: "sidecar"
  actions:
    - "Implementar abstracción HSMProvider"
    - "Configurar feature flags para modo"
    - "Documentar proceso migración"

phase_2_preparation:
  duration: "1-2 semanas" 
  architecture: "dual-mode"
  actions:
    - "Desarrollar cliente HSM remoto"
    - "Configurar Service para HSM dedicado"
    - "Testing paralelo ambos modos"

phase_2_migration:
  duration: "1 semana"
  architecture: "remote-hsm"
  actions:
    - "Deploy HSM StatefulSet dedicado"
    - "Migrar datos tokens (volumen PVC)"
    - "Switch config: sidecar → remote"
    - "Validar funcionamiento"
    - "Eliminar sidecar"

```
## Script de Migración Sencillo
```bash
#!/bin/bash
# migrate-hsm-mode.sh

# 1. Deploy HSM dedicado
kubectl apply -f k8s/softhsm-statefulset.yaml

# 2. Migrar volumen de tokens
kubectl cp $(kubectl get pods -l app=hsm-service -o name):/var/lib/softhsm/tokens/ ./tokens-backup/
kubectl cp ./tokens-backup/ $(kubectl get pods -l app=softhsm -o name):/var/lib/softhsm/tokens/

# 3. Cambiar configuración
kubectl set env deployment/hsm-service HSM_MODE=remote HSM_ENDPOINT=softhsm-service:9999

# 4. Verificar migración
kubectl rollout status deployment/hsm-service