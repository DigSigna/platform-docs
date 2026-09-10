# Roadmap de Transición

## Prerequisito
Antes de ejecutar esta migración, confirmar que el bug de persistencia de
`key_metadata_id` en `hsm_slots` (nunca se guardaba, quedaba `NULL`) está resuelto
y probado con un reinicio real del bootstrapper. Si no lo está, el HSM dedicado
nuevo va a fallar al reinicializar los slots exactamente igual que el sidecar
actual — migrar el volumen de tokens no soluciona ese problema, solo lo traslada.

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

## Rollback
El único paso sin vuelta atrás en el roadmap es "Eliminar sidecar" al final de
`phase_2_migration` hasta ese punto, revertir es solo repetir el switch de
configuración en sentido inverso:
 
1. NO eliminar el sidecar hasta validar operación con el HSM remoto por al menos
   [definir ventana, ej. 24-48h] bajo tráfico real.
2. Si falla después del corte pero antes de eliminar el sidecar:
   `kubectl set env deployment/hsm-service HSM_MODE=sidecar` y validar.
3. Si ya se eliminó el sidecar y falla: requiere restaurar desde el backup de
   `./tokens-backup/` generado en el paso 2 y redesplegar el sidecar, este
   camino es más lento, de ahí la importancia de no saltarse el punto 1.