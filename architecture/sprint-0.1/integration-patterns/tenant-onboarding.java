// // Servicio de provisioning de nuevos tenants
// @Service
// public class TenantProvisioningService {
    
//     @Transactional
//     public Tenant provisionTenant(TenantOnboardingRequest request) {
//         // 1. Crear schema en PostgreSQL
//         jdbcTemplate.execute("CREATE SCHEMA tenant_" + request.getTenantId());
        
//         // 2. Crear realm en Keycloak
//         keycloakAdminClient.createRealm(request.getTenantId());
        
//         // 3. Configurar API Gateway routes
//         gatewayClient.createRoute(request.getTenantId(), request.getDomains());
        
//         // 4. Generar API Keys iniciales
//         ApiKey initialKey = apiKeyService.generateInitialKeys(request.getTenantId());
        
//         // 5. Configurar HSM partition lógica
//         hsmAdminClient.createPartition(request.getTenantId());
        
//         return Tenant.builder()
//             .id(request.getTenantId())
//             .status(TenantStatus.ACTIVE)
//             .createdAt(Instant.now())
//             .build();
//     }
// }