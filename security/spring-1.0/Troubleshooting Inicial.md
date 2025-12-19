## Comandos de Diagnóstico
```bash
# Verificar políticas aplicadas
kubectl get networkpolicies --all-namespaces

# Diagnosticar conexiones bloqueadas
kubectl run network-test --image=nicolaka/netshoot -i --tty --rm

# Desde el pod de test:
nc -zv <service> <port>
dig <service>
```
## Procedimiento Rollback
```bash
# Si hay problemas, eliminar políticas gradualmente
kubectl delete networkpolicy default-deny-all
# O eliminar todas
kubectl delete networkpolicy --all