CATEGORÍA           | TECNOLOGÍA                  | JUSTIFICACIÓN MULTI-TENANT
-------------------|----------------------------|---------------------------
Backend Framework  | Spring Boot 3 + Spring Security | Mature multi-tenancy support, OAuth2 resource server
API Gateway        | Spring Cloud Gateway       | Programmable filters for tenant resolution, Java-native
Service Mesh       | Istio                      | Advanced mTLS, traffic policies per namespace
Identity           | Keycloak Multi-Realm       | Isolation per tenant, federation, custom themes
Database           | PostgreSQL 15 + Schemas    | Schema-per-tenant, row-level security, performance
Cache              | Redis Cluster + Key prefix | Isolated cache with eviction policies per tenant
Storage            | AWS S3 + Tenant prefixes   | Cost-effective, compliance features, lifecycle policies
HSM                | Thales Luna HSM + Partitions| Logical partitions per tenant, FIPS 140-2 L3