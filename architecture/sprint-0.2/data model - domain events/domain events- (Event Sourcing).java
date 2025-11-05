// // Eventos principales del sistema
// public interface DomainEvent {
//     String getEventId();
//     String getAggregateId();
//     String getAggregateType();
//     Instant getTimestamp();
//     String getTenantId();
//     String getUserId();
// }

// // Eventos de Identity
// public record UserRegisteredEvent(
//     String eventId,
//     String tenantId,
//     String userId,
//     String email,
//     Instant timestamp
// ) implements DomainEvent { }

// public record UserAuthenticatedEvent(
//     String eventId,
//     String tenantId,
//     String userId,
//     String authMethod,
//     Instant timestamp
// ) implements DomainEvent { }

// // Eventos de Certificados
// public record CertificateIssuedEvent(
//     String eventId,
//     String tenantId,
//     String certificateId,
//     String userId,
//     String serialNumber,
//     Instant validFrom,
//     Instant validTo,
//     Instant timestamp
// ) implements DomainEvent { }

// public record CertificateRevokedEvent(
//     String eventId,
//     String tenantId,
//     String certificateId,
//     String reason,
//     Instant timestamp
// ) implements DomainEvent { }

// // Eventos de Firma
// public record SigningSessionCreatedEvent(
//     String eventId,
//     String tenantId,
//     String sessionId,
//     String profile,
//     List<String> documentIds,
//     List<String> signerIds,
//     Instant timestamp
// ) implements DomainEvent { }

// public record DocumentSignedEvent(
//     String eventId,
//     String tenantId,
//     String sessionId,
//     String documentId,
//     String signerId,
//     String certificateId,
//     String signatureFormat,
//     Instant timestamp
// ) implements DomainEvent { }

// public record SigningSessionCompletedEvent(
//     String eventId,
//     String tenantId,
//     String sessionId,
//     String status, // COMPLETED, FAILED, CANCELLED
//     Instant timestamp
// ) implements DomainEvent { }