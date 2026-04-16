# ADR-020: Postergación de la implementación de OCSP en el Certificate Service

## Metadata
- **Autor:**
- **Fecha:** 15-01-2026 
- **Estado:** APPROVED
- **Revisores:**

## Status
ACCEPTED

## Context
En el desarrollo del MVP del sistema multitenant para firmas digitales, se ha definido un microservicio certificate-service responsable de la emisión, almacenamiento y consulta de certificados X.509. Durante el diseño, surgió la necesidad de que otros servicios (signing-service, verification-service) puedan verificar el estado de un certificado (vigente, revocado, expirado). Una opción es implementar el protocolo estándar OCSP (Online Certificate Status Protocol) desde el inicio, lo que permitiría interoperabilidad con clientes externos y cumplimiento de estándares PKI. Sin embargo, el MVP tiene un volumen estimado de 100-200 peticiones diarias y todos los consumidores iniciales serán microservicios internos del mismo sistema. La implementación de OCSP requiere manejo de ASN.1, construcción de respuestas firmadas, gestión de claves de firma para el responder y mayor complejidad en la infraestructura. Por otro lado, un endpoint REST simple que devuelva el estado del certificado en JSON es trivial de implementar y consumir.

## Decision
Se decide no implementar OCSP en la versión inicial (MVP) del certificate-service. En su lugar, se proveerá un endpoint REST simple (por ejemplo, GET /certificates/{id}/validity) que devuelva el estado del certificado. La lógica de negocio para determinar el estado se encapsulará en un caso de uso (GetCertificateStatusUseCase) dentro de la capa de aplicación, siguiendo la arquitectura hexagonal. De esta forma, cuando en el futuro se requiera soporte OCSP, se podrá añadir un adaptador (controlador) que utilice el mismo caso de uso, sin modificar el núcleo del dominio.

## Consecuencias
### Positivas
- Simplicidad y rapidez: El desarrollo del endpoint REST es inmediato y no requiere conocimientos especializados en ASN.1 ni en el protocolo OCSP.
- Menor complejidad operativa: No se necesita desplegar un responder OCSP ni gestionar sus claves y certificados.
- Alineación con el volumen esperado: Para 100-200 peticiones diarias, la sobrecarga de OCSP es innecesaria.
- Flexibilidad futura: La separación en capas (hexagonal) permite añadir soporte OCSP posteriormente como un adaptador adicional, sin alterar la lógica de negocio ni los consumidores internos que ya usen el endpoint REST (si se decide mantener ambos).

### Negativas
- No interoperabilidad con clientes externos estándar: Si en el futuro aparecen consumidores que exijan OCSP, será necesario implementarlo. Sin embargo, al estar preparada la arquitectura, el esfuerzo será acotado.
- Posible deuda técnica si se descuida la separación: Si no se respeta la arquitectura hexagonal y se acopla la lógica de negocio al endpoint REST, migrar a OCSP podría ser más costoso. Se mitigará mediante revisiones de código y adherencia al diseño.

Nota: Esta decisión se revisará cuando el sistema escale o cuando surjan requisitos de interoperabilidad explícitos.