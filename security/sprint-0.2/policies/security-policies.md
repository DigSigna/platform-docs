# Security Policies
**Sprint:** 0.2 | **Ámbito:** Template Security

## Authentication & Authorization
```yaml
Password Policy:
  algorithm: "BCrypt"
  cost_factor: 12
  min_length: 12
  require_special_chars: true
  max_age_days: 90
  history_size: 5

JWT Policy:
  algorithm: "RS256"  # NOTA: Actualmente HS256 - MIGRAR
  key_size: 2048
  access_token_expiry: "15m"
  refresh_token_expiry: "7d"
  issuer_validation: true
  audience_validation: false  # TODO: Habilitar en Sprint 1

Session Management:
  concurrent_sessions: 1
  automatic_logout: "12h"
  token_blacklist: "Redis"
```
## API Security
```yaml
Input Validation:
  - SQL Injection: "Prepared statements"
  - XSS: "Content-Security-Policy"
  - CSRF: "Stateless API (no requiere)"
  - Rate Limiting: "100 req/min por usuario"

Data Protection:
  encryption_at_rest: "AES-256-GCM"
  encryption_in_transit: "mTLS 1.2+"
  data_classification: "PII encryption required"

```

## Compliance Requirements
- OWASP ASVS: Level 2 compliance target
- GDPR: PII protection required
- NIST: Password guidelines compliance