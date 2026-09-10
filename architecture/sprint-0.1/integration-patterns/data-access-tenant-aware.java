// // Repository base con filtrado automático por tenant
// @Repository
// public abstract class TenantAwareRepository<T> {
    
//     @PersistenceContext
//     private EntityManager entityManager;
    
//     protected String getCurrentTenantId() {
//         return TenantContext.getCurrentTenant();
//     }
    
//     protected CriteriaQuery<T> addTenantFilter(CriteriaQuery<T> query, 
//                                              Root<T> root) {
//         return query.where(
//             cb.equal(root.get("tenantId"), getCurrentTenantId())
//         );
//     }
// }

// // Implementación concreta
// @Repository
// public class DocumentRepository extends TenantAwareRepository<Document> {
    
//     public List<Document> findByUser(String userId) {
//         CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//         CriteriaQuery<Document> query = cb.createQuery(Document.class);
//         Root<Document> root = query.from(Document.class);
        
//         query = addTenantFilter(query, root)
//             .where(cb.equal(root.get("userId"), userId));
            
//         return entityManager.createQuery(query).getResultList();
//     }
// }