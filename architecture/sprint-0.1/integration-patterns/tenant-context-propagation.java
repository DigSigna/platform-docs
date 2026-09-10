// // Filtro para resolver y propagar tenant context
// @Component
// public class TenantContextFilter implements Filter {
    
//     @Override
//     public void doFilter(ServletRequest request, ServletResponse response, 
//                        FilterChain chain) throws IOException, ServletException {
        
//         HttpServletRequest httpRequest = (HttpServletRequest) request;
//         String tenantId = resolveTenantId(httpRequest);
        
//         if (tenantId == null) {
//             throw new TenantResolutionException("Tenant no identificado");
//         }
        
//         // Validar tenant activo y licencia
//         tenantValidationService.validateActiveTenant(tenantId);
        
//         try {
//             TenantContext.setCurrentTenant(tenantId);
//             chain.doFilter(request, response);
//         } finally {
//             TenantContext.clear();
//         }
//     }
    
//     private String resolveTenantId(HttpServletRequest request) {
//         // 1. Header X-Tenant-ID + API Key validation
//         // 2. JWT claim extraction
//         // 3. Subdomain resolution  
//         // 4. Custom domain mapping
//     }
// }