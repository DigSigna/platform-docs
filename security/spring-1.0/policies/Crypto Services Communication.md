```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-crypto-services
  namespace: default
spec:
  podSelector:
    matchLabels:
      app.kubernetes.io/component: crypto-service
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app.kubernetes.io/name: api-gateway
    - podSelector:
        matchLabels:
          app.kubernetes.io/component: signing-service