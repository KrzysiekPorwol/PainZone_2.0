# Quality — PainZone 2.0

> TL;DR: DoD ficzy MVP + scenariusze jakości (NFR) odwołujące się do PRD AC. Integration testy odroczone do v1.1.

## Spec

### Definition of Done — ficza MVP
1. Wireframe istnieje w `docs/04-*.md`.
2. Encje + invarianty w `docs/05-*.md`. Każda non-obvious decyzja techniczna → ADR w `docs/adr/`.
3. Unit testy invariantów domain (pure Kotlin, brak Android deps).
4. Compose `@Preview` dla każdego stanu ekranu (loading / empty / content / error).
5. Commit lokalny przez skill `commit` (EN+PL).
6. Manual smoke test golden path zaliczony przez usera (skill `verify`).
7. Push na `origin/main` (po smoke OK).

### Definition of Done — task (sesja)
Patrz `.claude/rules/workflow.md#Definition-of-Done-task`. DoD ficzy = DoD wszystkich tasków + smoke E2E milestone.

### Quality Scenarios (NFR)

| Wymiar | Scenariusz | Próg | Źródło |
|--------|-----------|------|--------|
| Performance | Tap „Zapisz serię" → zapis widoczny | ≤3s mediana z 10 logowań | PRD A2 |
| Performance | Filtr Stats 30d/90d/rok/all przełącz | <200ms re-render | PRD US-6 |
| Performance | Cold start na średnim telefonie (Pixel 6a) | <2s do interaktywności | — |
| Reliability | Proces killed w środku sesji → wznowienie | `InProgress` odtworzony, brak utraty serii | PRD US-3 |
| Reliability | Pauza w sesji 30min → powrót | `InProgress` zachowany | PRD US-3 |
| Reliability | Restart telefonu | Pełna historia + plany dostępne | PRD US-7 |
| Offline | Airplane mode od instalacji | 100% funkcjonalności | PRD US-7 |
| Data | Soft delete `Exercise` | Historyczne sesje read-only z markerem „usunięte" | PRD US-1, US-6 |
| Compatibility | minSdk | 26 (Android 8 Oreo) | `06-architecture.md` |
| UX | Bottom bar wygląd | Aesthetic checkpoint w M0.4 — akcept usera przed kodem | memory: phase4 bottom bar |

### Out of scope quality (post-MVP, v1.1+)
- Integration testy (in-memory Room) — odroczone z MVP. Decyzja: solo learning project, unit testy domain + manual smoke wystarczają.
- Crash reporting (Firebase Crashlytics / Sentry) — v1.1 po pierwszym feedbacku userów.
- Automated UI tests (Compose UI / Espresso) — v1.1 po stabilizacji UX.
- Analytics — nigdy (privacy by default, zgodnie z `01-vision.md#Non-Goals`).

## Referencje

`07-roadmap.md` (milestones) · `rules.md` (workflow sesji) · `02-prd.md` (źródło AC i assumption tests)