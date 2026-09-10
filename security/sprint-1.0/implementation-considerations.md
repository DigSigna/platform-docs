## Dependencias del CNI
```yaml
cni_requirements:
  - "DO Kubernetes usa kube-router por defecto (soporta Network Policies)"
  - "Verificar: kubectl get daemonset -n kube-system"
  - "Alternativa: Cambiar a Calico si se necesitan políticas más avanzadas"
```
## Impacto en Desarrollo
```yaml
development_considerations:
  - "Las policies pueden bloquear conexiones de debugging"
  - "Considerar namespace 'dev' sin policies restrictivas"
  - "Tools como Lens/Octant pueden ayudar a visualizar conexiones"