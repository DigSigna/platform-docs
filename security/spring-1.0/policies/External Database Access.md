```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-external-database
  namespace: default
spec:
  podSelector:
    matchLabels:
      app.kubernetes.io/component: microservice
  policyTypes:
  - Egress
  egress:
  - to:
    - ipBlock:
        cidr: 0.0.0.0/0
    ports:
    - port: 5432
      protocol: TCP
  - to:
    - ipBlock:
        cidr: 0.0.0.0/0
    ports:
    - port: 6379
      protocol: TCP
```

**Nota**: 
Reemplazar 0.0.0.0/0 con los IPs específicos de DO Managed Databases