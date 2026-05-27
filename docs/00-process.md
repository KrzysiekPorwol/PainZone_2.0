# Proces designu — PainZone 2.0

> TL;DR: Meta-dokument — statusy faz i jednozdaniowe cele. Outputy: `docs/01-vision.md` … `docs/08-quality.md`.

## Status

| Faza | Deliverable | Status |
|------|-------------|--------|
| 0. Kontrakt projektu | `CLAUDE.md` | 🟢 done |
| 1. Vision & Discovery | `docs/01-vision.md` | 🟢 done |
| 2. PRD + OST | `docs/02-prd.md` | 🟢 done |
| 3. User Flows + IA | `docs/03-flows.md` | 🟢 done |
| 4. Wireframes Lo-Fi | `docs/04-wireframes.md` | 🟢 done |
| 5. Domain Model | `docs/05-domain.md` | 🟢 done |
| 6. Architektura + ADR + Threat Model | `docs/06-architecture.md`, `docs/adr/*`, `docs/threat-model.md` | 🟢 done |
| 7. Roadmap + jakość | `docs/07-roadmap.md`, `docs/08-quality.md`, `docs/rules.md` | 🟢 done |

Artefakty żywe: `docs/glossary.md` · `docs/adr/` · `docs/rules.md`

## Fazy — cele

| Faza | Cel jednozdaniowo |
|------|-------------------|
| 0 | Kontrakt Claude ↔ Krzysiek — styl, decision authority, git. |
| 1 | Co, dla kogo, dlaczego, metryki, non-goals. |
| 2 | MoSCoW + OST (outcome → solutions) + User Stories z AC. |
| 3 | Inwentarz ekranów + nawigacja + flowy 4 top scenariuszy. |
| 4 | Lo-fi frame każdego ekranu — struktura, stany, interakcje. |
| 5 | Encje, relacje, stany, invarianty — serce apki. |
| 6 | Tech stack z ADR per decyzja + threat model 1-strona. |
| 7 | Milestones + scenariusze jakości + Definition of Done. |

## Definition of Done — faza designu

Przed Fazą 8 (scaffolding kodu) wszystkie 4 true:
1. Każda ficza MVP ma wireframe i encję w domain modelu.
2. Każda encja ma typowane atrybuty, każda znacząca decyzja ma ADR.
3. MVP realnie do zbudowania solo.
4. `docs/` opowiada historię end-to-end — senior reviewer rozumie *co* i *dlaczego* w 5 min.