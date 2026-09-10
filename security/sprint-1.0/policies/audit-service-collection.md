```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-audit-ingress
  namespace: default
spec:
  podSelector:
    matchLabels:
      app.kubernetes.io/name: audit-service
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app.kubernetes.io/component: microservice
    ports:
    - port: 8080