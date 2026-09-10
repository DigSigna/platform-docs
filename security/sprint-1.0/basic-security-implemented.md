security_baseline:
  - network_policies: "default-deny con excepciones funcionales"
  - non_root_containers: "usuario 1000 para todos los pods"
  - volume_isolation: "tokens HSM en volúmenes aislados"
  - secrets_management: "Kubernetes Secrets + encryption at rest"