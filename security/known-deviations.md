# Known Deviations Ledger

> **Purpose:** this is not an architecture spec, it's an honest record of where the
> actual implementation diverges from what other documents in this repository claim
> should happen. It lives outside the sprint-based structure on purpose: it's a living
> document that gets updated as gaps close or appear, not a point-in-time snapshot.
>
> Golden rule: if a document under `architecture/` or `security/` claims something
> ("keys are never hardcoded", "FIPS 140-2 from Phase 1") and reality differs, the gap
> gets logged here it is not silently ignored, and the original claim is not quietly
> edited to match reality instead.
>
> **Naming note:** decision records referenced below use two prefixes depending on when
> they were written. `DLT-NNN` = Stage 1 records (Spanish, original Metadata-block shape,
> frozen as historical record). `ADR-NNN` = Stage 2 onward (English, canonical template
> in `standards/adr-template.md`). Both are valid references, the prefix just tells you
> which stage produced the decision, not which one is "more correct."

## How to use this table
- **Status:** `OPEN` (gap exists, no plan yet) · `TRACKED` (exists, has a plan/issue) ·
  `MITIGATED` (risk reduced, not fully closed) · `ACCEPTED RISK` (conscious decision not
  to close it, with justification) · `CLOSED` (resolved, kept for traceability)
- **Source:** where the finding came from (code review, this repo's own audit, etc.)

| ID | Area | Deviation | Document/decision it contradicts | Source | Severity | Status |
|----|------|-----------|-----------------------------------|--------|----------|--------|
| KD-001 | hsm-service | Hardcoded default AES master key in `app_config.go`, active if the env var isn't overridden | `security/sprint-1.0/secrets-management-architecture.md` "never in the database, only metadata" (same principle extends to never-in-code) | hsm-service code review, 2026-09-08 | Critical | OPEN |
| KD-002 | hsm-service | HSM slot PINs and cryptographic material printed to logs (`println`/`log.Printf`) across multiple files | Implicit secret-handling principle in `secrets-management-architecture.md`; the code repo's own `AGENTS.md` | hsm-service code review, 2026-09-08 | Critical | OPEN |
| KD-003 | hsm-service | Slot PINs generated with `math/rand` instead of `crypto/rand` | No documented decision explicitly allows this; it's a basic cryptographic hygiene gap | hsm-service code review, 2026-09-08 | Critical | OPEN |
| KD-004 | hsm-service | Admin endpoints (`/internal/hsm/*`) have no authorization check any valid JWT can delete/create slots for any tenant | `security/sprint-1.0/policies/policy-default-deny-base.md` and the rest of the communication matrix assume granular access control | hsm-service code review, 2026-09-08 | Critical | OPEN |
| KD-005 | hsm-service | `hsm_slots.key_metadata_id` is never persisted (hardcoded `NULL`) every slot fails to reinitialize after a restart | `secrets-management-architecture.md` describes a key-version flow that depends on this persistence | hsm-service code review, 2026-09-08 | Critical | OPEN blocks the migration plan in `hsm/sprint-1.0/controlled-migration-plan-phase-2.md` |
| KD-006 | Compliance | FIPS 140-2 Level 1 achieved (not Level 3); NOM-151 pending external auditor validation | `architecture/sprint-0.1/decision-log/DLT-009-compliance-strategy.md` (original ambition) | DLT-013, this document's own review | High | ACCEPTED RISK conscious tradeoff for MVP budget/timeline, see DLT-009/DLT-013 |
| KD-007 | QA | Real test coverage ≈0% in hsm-service (2 test files, one of them broken) vs. the 80%+ target in `qa/sprint-0.2/identity-automation-plan.md` | Documented QA coverage target | hsm-service code review, 2026-09-08 | High | OPEN |
| KD-008 | Security | Key rotation runbook (DLT-019) documented but never dry-run tested | Acceptance criterion added in DLT-019 | This document's own review | Medium | TRACKED acceptance criterion defined, execution still pending |
| KD-009 | Security | Threat models (`security/sprint-0.2/threat-modeling/`) were written before the SoftHSM/polyglot pivot; several mitigations assumed hardware HSM | DLT-013 (SoftHSM adoption) | This document's own review | Medium | MITIGATED note added to `02-certificate-keys-layer-threat-model.md`; 01/03/04/05 not yet reviewed |

## Notes
- This ledger doesn't replace actually fixing the code the `hsm-service` rows (KD-001
  through KD-005) close when the corresponding fix from the code review is applied and
  verified, not when this file gets edited.
- When a row moves to `CLOSED`, it's kept (not deleted) with the closure date, so the
  history of "how often we drift from what's documented" stays auditable over time.