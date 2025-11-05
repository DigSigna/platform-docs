// Servicio de migración de tenants existentes
@Service
@Transactional
public class TenantMigrationService {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public MigrationResult migrateLegacyTenant(String legacyTenantId, String newTenantId) {
        MigrationResult result = new MigrationResult();
        
        try {
            // 1. Crear nuevo schema
            createTenantSchema(newTenantId);
            
            // 2. Migrar usuarios
            migrateUsers(legacyTenantId, newTenantId, result);
            
            // 3. Migrar certificados  
            migrateCertificates(legacyTenantId, newTenantId, result);
            
            // 4. Migrar documentos (S3)
            migrateDocuments(legacyTenantId, newTenantId, result);
            
            // 5. Actualizar referencias
            updateReferences(legacyTenantId, newTenantId);
            
            result.setStatus(MigrationStatus.COMPLETED);
            
        } catch (Exception e) {
            result.setStatus(MigrationStatus.FAILED);
            result.setError(e.getMessage());
            // Rollback procedures
            rollbackMigration(newTenantId);
        }
        
        return result;
    }
    
    private void migrateUsers(String legacyTenantId, String newTenantId, MigrationResult result) {
        // Lógica de migración de usuarios
        // Incluye transformación de datos y hash de contraseñas
    }
}