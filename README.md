# DigSigna - Platform Documentation

Source of truth for the DigSigna platform: a multi-tenant SaaS **digital signature** system (cryptographic document signing) built on polyglot microservices.

This repository holds cross-service architecture decisions, security and threat-modeling documentation, QA strategy, and shared standards. It does not contain application code; see the individual service repositories (`hsm-service`, `migrations`, ...) for implementation.

## Structure

| Folder | Contents |
|---|---|
| `architecture/` | Architecture documents, Technical Decision Log (`DLT-*`), diagrams, API specs, per-sprint snapshots |
| `security/` | Threat models, cryptographic specification, network policies, secrets management |
| `qa/` | Testing strategy, acceptance criteria, tooling setup |
| `standards/` | Shared templates (ADR, architecture doc) and documentation conventions |

## Conventions

- **File and folder names**: `kebab-case`, English, ASCII only, no spaces, no accents. This keeps paths portable across tools, scripts, and CI.
- **Document content**: existing documents remain in their original language (mostly Spanish) and are not being retroactively translated. New documents are written in English going forward.
- **Decision records**: technical decisions go under `architecture/*/decision-log*/DLT-NNN-*.md`, following `standards/adr-template.md`. Every entry needs an `Estado` (status) field; a decision that replaces an earlier one must mark the older entry as `SUPERSEDED`, not leave both as `APPROVED`.

## Quality gates

CI (`.github/workflows/docs-ci.yml`) runs on every push/PR and checks:

- **Markdown links.** `markdown-link-check`
- **Diagrams.** presence and non-empty `.puml` files
- **Secret scanning.** [gitleaks](https://github.com/gitleaks/gitleaks), configured in `.gitleaks.toml`
- **Spelling.** `codespell` (informational, non-blocking, see note below)

Spelling is intentionally non-blocking: this repo mixes Spanish and English content by design (see Conventions above), and codespell's English dictionary isn't tuned for that. Treat its output as a hint, not a gate.

## Contributing

1. New documents follow the naming convention above.
2. Technical decisions go through an ADR (`standards/adr-template.md`) before being marked `APPROVED`.
3. Run `gitleaks detect --source . --config .gitleaks.toml` locally before pushing if you're adding anything that looks like a credential, sample key, or token.