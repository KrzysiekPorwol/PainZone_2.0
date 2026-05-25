za# Design Process — PainZone 2.0

> Meta-document: **how** we design this app before writing code.
> Phase outputs live in `docs/01-vision.md` … `docs/07-roadmap.md`.
> This file is a living reference — it evolves when we learn the process needs to evolve.

## Status

| Phase | Deliverable | Status |
|-------|-------------|--------|
| 0. Setup project contract | `CLAUDE.md` (live state, collab rules, non-goals) | 🟡 in-progress |
| 1. Vision & Discovery | `docs/01-vision.md` | ⏳ pending (draft exists, revision blocked until Phase 0 done) |
| 1.5. Risks & Competitive | `docs/risks-and-assumptions.md`, `docs/competitive-analysis.md` | ⏳ pending |
| 2. PRD + OST | `docs/02-prd.md` | ⏳ pending |
| 3. User Flows & IA | `docs/03-flows.md` | ⏳ pending |
| 4. Wireframes (Lo-Fi) | `docs/04-wireframes.md` + Figma link | ⏳ pending |
| 5. Domain Model | `docs/05-domain.md` | ⏳ pending |
| 6. Architecture + ADRs + Threat Model | `docs/06-architecture.md`, `docs/adr/*`, `docs/threat-model.md` | ⏳ pending |
| 6.5. Walking Skeleton | `docs/walking-skeleton.md` | ⏳ pending |
| 7. Roadmap + Quality Scenarios | `docs/07-roadmap.md`, `docs/08-quality.md` | ⏳ pending |

**Living artifacts** (grow across phases, not gated to one phase):
- `docs/glossary.md` — ubiquitous language (starts Phase 1)
- `docs/adr/` — Architecture Decision Records (starts whenever a non-trivial decision is made)

---

## Philosophy

1. **Design-first.** Every phase produces a Markdown artifact in `docs/`. No code until phases 1–7 pass the "Definition of Done" below.
2. **Every decision is justified.** Trivial choices in commit messages, medium ones in inline doc notes, significant ones in ADRs (Nygard format: Context / Decision / Consequences).
3. **Cheap iteration first.** Wireframes before Compose. Domain model before Room schema. Architecture diagram before module structure.
4. **Architecture follows requirements.** MVVM / single-module / Hilt because *of specific reasons*, not because "everyone uses it".
5. **Process itself is a portfolio artifact.** A recruiter opening `docs/` should grasp the discipline in 5 minutes.

---

## Phases

### Phase 0 — Setup project contract

**Goal:** Establish `CLAUDE.md` as the always-loaded contract — every future session starts with the same orientation about project context, collaboration style, decision authority, and locked non-goals.
**Activities:** Add sections to `CLAUDE.md`: (A) Project context — one paragraph: what it is, stack, hard constraints; (B) Collaboration style — Socratic dialog, direct opinions with alternatives, justify everything; (C) When Claude acts vs asks — mechanics → act, design/scope/architecture → ask; (D) Locked non-goals — short list mirrored from Vision. Each section must earn its place by being useful in *every* session — heavy/phase-specific content stays in `docs/`.
**Deliverable:** Updated `CLAUDE.md` (~70 lines total, well under the ~200-line danger zone).
**Sources:** Anthropic Claude Code docs (CLAUDE.md convention); team agreements literature (Lyssa Adkins *Coaching Agile Teams*).

### Phase 1 — Vision & Discovery

**Goal:** One-sentence answers to: what is it, for whom, why, success metric, non-goals.
**Activities:** Lean Canvas / Vision Statement interview, persona, problem statement, USP, success metrics, non-goals. Kick off `glossary.md` with first 10 domain terms.
**Deliverable:** `docs/01-vision.md`
**Sources:** Eric Ries *The Lean Startup*; Ash Maurya *Running Lean* (Lean Canvas); Jonathan Rasmusson *The Agile Samurai* (Inception Deck).

### Phase 1.5 — Risks & Competitive

**Goal:** Surface what could kill the project, and map the competitive landscape — before locking scope.
**Activities:**
- **Assumptions & Risks register** — list every assumption (about user, self, tech) that *might be false*. For each: likelihood, impact, mitigation, validation experiment.
- **Competitive teardown** — Hevy, Strong, Jefit: 1 screenshot per app, table of "what they do well / poorly / where the gap is". Locks USP.
**Deliverables:** `docs/risks-and-assumptions.md`, `docs/competitive-analysis.md`
**Sources:** arc42 §11 Risks; Marty Cagan *Inspired* (framing risks); Teresa Torres *Continuous Discovery Habits* (assumption tests).

### Phase 2 — PRD + Opportunity Solution Tree

**Goal:** Prioritized feature scope and a visual map from outcome to solutions.
**Activities:**
- **Opportunity Solution Tree (OST)** — outcome → opportunities (user needs) → solutions → assumption tests. Mermaid diagram.
- **MoSCoW prioritization** — MVP (Must) / v1.1 (Should) / v2 (Could) / Won't.
- **User stories** for MVP: *As [persona], I want [action], so that [benefit]*, each with acceptance criteria.
**Deliverable:** `docs/02-prd.md`
**Sources:** Teresa Torres *Continuous Discovery Habits* (OST); Karl Wiegers *Software Requirements* (MoSCoW, acceptance criteria).

### Phase 3 — User Flows & Information Architecture

**Goal:** All screens + how users move between them.
**Activities:** Screen inventory; user flows in Mermaid for top scenarios (first launch, start workout, edit plan, see progress); navigation map; IA decision (bottom bar / drawer / tabs).
**Deliverable:** `docs/03-flows.md`
**Sources:** Donald Norman *The Design of Everyday Things*; Steve Krug *Don't Make Me Think*.

### Phase 4 — Wireframes (Lo-Fi)

**Goal:** Low-fidelity sketch of every screen. Structure only, no colors or final typography.
**Activities:** Figma free account; one lo-fi frame per screen from Phase 3; annotations for click behavior; iterate on cheap medium.
**Deliverable:** `docs/04-wireframes.md` (Figma link + key screenshots)
**Sources:** Bill Buxton *Sketching User Experiences*.

### Phase 5 — Domain Model

**Goal:** Entities, attributes, relationships, states. The heart of the app.
**Activities:**
- Promote `glossary.md` to "mature" — every domain term defined with examples.
- **Entity-Relationship Diagram** in Mermaid: entities (Exercise, MuscleGroup, TrainingPlan, PlannedDay, PlannedExercise, WorkoutSession, LoggedSet…), typed attributes, relations.
- **State machines** for stateful entities (e.g. WorkoutSession: `NotStarted → InProgress → Paused → Completed → Discarded`).
- **Invariants & edge cases** — e.g. "cannot edit a completed session", "LoggedSet reps > 0".
- Locked decisions: muscle group as enum vs table; units (kg/lb) global vs per-exercise; ID strategy (UUID vs auto-increment).
**Deliverable:** `docs/05-domain.md`
**Sources:** Eric Evans *Domain-Driven Design* (Ubiquitous Language); Vaughn Vernon *Implementing DDD*; Vlad Khononov *Learning Domain-Driven Design* (2021).

### Phase 6 — Architecture, ADRs & Threat Model

**Goal:** Architectural choices, each justified with an ADR.
**Activities:**
- **C4 diagrams** — Context (System + actors) and Container (Android app + Room DB + future cloud). Component diagram optional, only if useful.
- **Tech stack with rationale** (each as an ADR): architecture style (MVVM / MVI / Clean-lite), modularization (single vs multi), DI (Hilt / Koin / manual), navigation (Navigation Compose type-safe routes), persistence (Room + KSP), async (Coroutines + Flow), preferences (DataStore), test stack (JUnit + MockK + Turbine + Compose UI test), charts (Vico / MPAndroidChart / custom).
- **Cross-cutting concerns:** logging (Timber?), error handling (sealed result types?), theming (Material 3 dynamic colors).
- **Threat model** (1 page) — LINDDUN-lite for privacy-sensitive health/training data: what data, where stored, who could see it, what's mitigated, what's accepted risk. Even "nothing leaves the device" is a valid posture — but written.
**Deliverables:** `docs/06-architecture.md` (overview + C4 diagrams); `docs/adr/0001-architecture-style.md`, `0002-modularization.md`, `0003-di-framework.md`, … (4–8 ADRs total); `docs/threat-model.md`
**Sources:** Simon Brown *Software Architecture for Developers* (C4); Michael Nygard "Documenting Architecture Decisions" (2011); arc42 template; OWASP SAMM Threat Assessment; LINDDUN framework (KU Leuven).

### Phase 6.5 — Walking Skeleton plan

**Goal:** Plan an end-to-end thinnest-possible vertical slice (one screen → ViewModel → Room → back) **before** committing to a full sprint of features. Validates that the architecture from Phase 6 actually composes.
**Activities:** Pick the simplest meaningful slice (likely: "Add an exercise to the library, see it in the list"). Define what's in, what's stubbed, what's deferred. This is a *plan*, not yet code.
**Deliverable:** `docs/walking-skeleton.md`
**Sources:** Alistair Cockburn (1999) "Walking Skeleton"; Hunt & Thomas *The Pragmatic Programmer* (tracer bullets).

### Phase 7 — Roadmap, Quality Attribute Scenarios & Definition of Done

**Goal:** Iteration plan + measurable quality bar.
**Activities:**
- Milestone breakdown: M1 (scaffolding + exercise library), M2 (CRUD plans), M3 (workout session + LoggedSet), M4 (statistics), M5 (polish + Play release).
- **Quality Attribute Scenarios** in arc42 §10 format: *Source / Stimulus / Artifact / Environment / Response / Response Measure* — for performance (e.g. "log a set in < 200ms p95"), reliability, usability, security, maintainability.
- **Definition of Done** for features (tests pass, no Detekt warnings, screenshot in PR, ADR if architectural).
**Deliverables:** `docs/07-roadmap.md`, `docs/08-quality.md`

---

## Definition of Done — design phase

Before scaffolding code (Phase 8), all four must be true:

1. **Coverage:** every MVP feature in PRD is covered by a wireframe and at least one domain entity.
2. **Completeness:** every screen has a flow, every entity has typed attributes, every significant decision has an ADR.
3. **Feasibility:** MVP is realistically buildable in the planned milestones, given solo capacity.
4. **Portfolio-readiness:** `docs/` tells the story end-to-end such that a senior reviewer needs ≤ 5 minutes to grasp *what* and *why*.

If any = false → return to the relevant phase.

---

## Tools

| Tool | Use | Cost |
|------|-----|------|
| Markdown | All `docs/*.md` artifacts | 0 |
| Mermaid | ER, flows, state machines, OST, C4-lite (renders in GitHub) | 0 |
| Figma (free) | Lo-fi wireframes | 0 |
| ADR (Nygard) | Architecture decisions, `adr-tools` numbering convention (`0001-*.md`) | 0 |
| GitHub Actions | CI: lint + unit tests + instrumented tests on hosted emulator | 0 |
| mkdocs-material | (optional) Publish `docs/` as a site on GitHub Pages | 0 |

## Portfolio polish (2025 table stakes)

These are not phase deliverables but are expected by senior Android reviewers:

- C4 Context + Container diagrams in Phase 6
- ADRs numbered `0001-*.md`, `0002-*.md`, … (`adr-tools` convention)
- GitHub Actions CI + status badge in README
- Module graph diagram (`./gradlew projectDependencyGraph`)
- Screen recording / GIF in README + APK published to GitHub Releases
- Conventional Commits + auto-generated `CHANGELOG.md`

## How we work in practice

Per phase: (1) I ask questions Socratically, (2) you decide / correct, (3) I draft the artifact, (4) we review together, (5) commit + push per `CLAUDE.md` (bilingual EN+PL), (6) next phase. Each phase usually = 2–4 dialog sessions. Iterative, not waterfall.

## References

- Project rules: `CLAUDE.md`
- Phase 1 output: `docs/01-vision.md`
- Living glossary: `docs/glossary.md`
- ADRs: `docs/adr/`

---

# Proces designu — PainZone 2.0

> Meta-dokument: **jak** projektujemy tę apkę przed napisaniem kodu.
> Outputy faz mieszkają w `docs/01-vision.md` … `docs/07-roadmap.md`.
> Ten plik to żywy referens — ewoluuje, gdy uznamy że proces powinien się zmienić.

## Status

| Faza | Deliverable | Status |
|------|-------------|--------|
| 0. Setup kontraktu projektu | `CLAUDE.md` (live state, reguły współpracy, non-goals) | 🟡 w toku |
| 1. Vision & Discovery | `docs/01-vision.md` | ⏳ przed nami (draft istnieje, rewizja po zamknięciu Fazy 0) |
| 1.5. Ryzyka i konkurencja | `docs/risks-and-assumptions.md`, `docs/competitive-analysis.md` | ⏳ przed nami |
| 2. PRD + OST | `docs/02-prd.md` | ⏳ przed nami |
| 3. User Flows + IA | `docs/03-flows.md` | ⏳ przed nami |
| 4. Wireframes (Lo-Fi) | `docs/04-wireframes.md` + link do Figmy | ⏳ przed nami |
| 5. Domain Model | `docs/05-domain.md` | ⏳ przed nami |
| 6. Architektura + ADR + Threat Model | `docs/06-architecture.md`, `docs/adr/*`, `docs/threat-model.md` | ⏳ przed nami |
| 6.5. Walking Skeleton | `docs/walking-skeleton.md` | ⏳ przed nami |
| 7. Roadmap + scenariusze jakości | `docs/07-roadmap.md`, `docs/08-quality.md` | ⏳ przed nami |

**Artefakty żywe** (rosną przez wiele faz, nie są związane z jedną):
- `docs/glossary.md` — ubiquitous language (start: Faza 1)
- `docs/adr/` — Architecture Decision Records (start: gdy pada pierwsza nietrywialna decyzja)

---

## Filozofia

1. **Design najpierw.** Każda faza produkuje plik Markdown w `docs/`. Żadnego kodu dopóki fazy 1–7 nie przejdą "Definition of Done" poniżej.
2. **Każda decyzja udokumentowana.** Trywialne — w commit message; średnie — inline w doc; znaczące — ADR (format Nygarda: Context / Decision / Consequences).
3. **Najpierw tania iteracja.** Wireframes przed Compose. Domain model przed schematem Room. Diagram architektury przed strukturą modułów.
4. **Architektura wynika z wymagań.** MVVM / single-module / Hilt bo *konkretne powody*, a nie bo "każdy używa".
5. **Sam proces jest artefaktem portfolio.** Recruiter otwierający `docs/` powinien wyczuć dyscyplinę w 5 minut.

---

## Fazy

### Faza 0 — Setup kontraktu projektu

**Cel:** Ustawienie `CLAUDE.md` jako always-loaded kontraktu — każda przyszła sesja startuje z tą samą orientacją: kontekst projektu, styl współpracy, decision authority, locked non-goals.
**Działania:** Dodać sekcje do `CLAUDE.md`: (A) Kontekst projektu — akapit: co to, stack, twarde constraintsy; (B) Sposób pracy — sokratejski dialog, opinie wprost z alternatywą, uzasadniaj wszystko; (C) Kiedy Claude działa vs pyta — mechanika → działa, design/scope/architektura → pyta; (D) Twarde non-goals — krótka lista zmirrorowana z Vision. Każda sekcja musi zarabiać miejsce przez bycie użyteczną w *każdej* sesji — ciężka/specyficzna dla fazy zawartość zostaje w `docs/`.
**Deliverable:** Zaktualizowany `CLAUDE.md` (~70 linii razem, mocno poniżej strefy ~200 linii).
**Źródła:** dokumentacja Claude Code Anthropic (konwencja CLAUDE.md); literatura team agreements (Lyssa Adkins *Coaching Agile Teams*).

### Faza 1 — Vision & Discovery

**Cel:** Jednozdaniowe odpowiedzi: co to, dla kogo, dlaczego, metryka sukcesu, non-goals.
**Działania:** Wywiad w stylu Lean Canvas / Vision Statement, persona, problem, USP, metryki sukcesu, non-goals. Start `glossary.md` od pierwszych ~10 terminów domenowych.
**Deliverable:** `docs/01-vision.md`
**Źródła:** Eric Ries *The Lean Startup*; Ash Maurya *Running Lean* (Lean Canvas); Jonathan Rasmusson *The Agile Samurai* (Inception Deck).

### Faza 1.5 — Ryzyka i konkurencja

**Cel:** Wyciągnąć na wierzch co może zabić projekt + zmapować konkurencję — zanim zamkniemy scope.
**Działania:**
- **Assumptions & Risks register** — lista każdego założenia (o userze, o sobie, o technologii) które *może okazać się fałszywe*. Dla każdego: prawdopodobieństwo, impact, mitigation, eksperyment walidujący.
- **Competitive teardown** — Hevy, Strong, Jefit: 1 screenshot per apka, tabela "co robią dobrze / źle / gdzie luka". Domyka USP.
**Deliverables:** `docs/risks-and-assumptions.md`, `docs/competitive-analysis.md`
**Źródła:** arc42 §11 Risks; Marty Cagan *Inspired* (framing risks); Teresa Torres *Continuous Discovery Habits* (assumption tests).

### Faza 2 — PRD + Opportunity Solution Tree

**Cel:** Priorytetyzowany scope + wizualna mapa od outcome do rozwiązań.
**Działania:**
- **Opportunity Solution Tree (OST)** — outcome → opportunities (potrzeby usera) → solutions → assumption tests. Diagram w Mermaid.
- **MoSCoW** — MVP (Must) / v1.1 (Should) / v2 (Could) / Won't.
- **User stories** dla MVP: *Jako [persona], chcę [akcja], żeby [benefit]*, każda z acceptance criteria.
**Deliverable:** `docs/02-prd.md`
**Źródła:** Teresa Torres *Continuous Discovery Habits* (OST); Karl Wiegers *Software Requirements* (MoSCoW, acceptance criteria).

### Faza 3 — User Flows & Information Architecture

**Cel:** Wszystkie ekrany + jak user się między nimi porusza.
**Działania:** Inwentarz ekranów; flowy w Mermaid dla top scenariuszy (pierwsze uruchomienie, zacznij trening, edytuj plan, zobacz progres); navigation map; decyzja IA (bottom bar / drawer / tabs).
**Deliverable:** `docs/03-flows.md`
**Źródła:** Donald Norman *The Design of Everyday Things*; Steve Krug *Don't Make Me Think*.

### Faza 4 — Wireframes (Lo-Fi)

**Cel:** Low-fidelity szkic każdego ekranu. Tylko struktura — bez kolorów i finalnej typografii.
**Działania:** Darmowe konto Figma; jeden lo-fi frame per ekran z Fazy 3; adnotacje "co po kliknięciu czego"; iteracja na tańszej warstwie.
**Deliverable:** `docs/04-wireframes.md` (link do Figmy + screenshoty kluczowych ekranów)
**Źródła:** Bill Buxton *Sketching User Experiences*.

### Faza 5 — Domain Model

**Cel:** Encje, atrybuty, relacje, stany. Serce apki.
**Działania:**
- Promocja `glossary.md` do "mature" — każdy termin domenowy z definicją i przykładem.
- **Entity-Relationship Diagram** w Mermaid: encje (Exercise, MuscleGroup, TrainingPlan, PlannedDay, PlannedExercise, WorkoutSession, LoggedSet…), typowane atrybuty, relacje.
- **State machines** dla bytów ze stanem (np. WorkoutSession: `NotStarted → InProgress → Paused → Completed → Discarded`).
- **Invariants i edge cases** — np. "nie można edytować ukończonej sesji", "LoggedSet reps > 0".
- Decyzje do zamknięcia: grupa mięśniowa jako enum vs tabela; jednostki (kg/lb) globalnie vs per ćwiczenie; strategia ID (UUID vs auto-increment).
**Deliverable:** `docs/05-domain.md`
**Źródła:** Eric Evans *Domain-Driven Design* (Ubiquitous Language); Vaughn Vernon *Implementing DDD*; Vlad Khononov *Learning Domain-Driven Design* (2021).

### Faza 6 — Architektura, ADR-y i Threat Model

**Cel:** Wybory architektoniczne, każdy uzasadniony ADR-em.
**Działania:**
- **Diagramy C4** — Context (system + aktorzy) i Container (apka Android + baza Room + ewentualny przyszły cloud). Component opcjonalnie, tylko jeśli przydatny.
- **Tech stack z uzasadnieniem** (każdy jako ADR): styl architektury (MVVM / MVI / Clean-lite), modularizacja (single vs multi), DI (Hilt / Koin / manual), nawigacja (Navigation Compose type-safe), persystencja (Room + KSP), async (Coroutines + Flow), preferencje (DataStore), test stack (JUnit + MockK + Turbine + Compose UI test), charts (Vico / MPAndroidChart / custom).
- **Cross-cutting concerns:** logging (Timber?), error handling (sealed result types?), theming (Material 3 dynamic colors).
- **Threat model** (1 strona) — LINDDUN-lite dla danych treningowych/zdrowotnych: jakie dane, gdzie przechowywane, kto mógłby je zobaczyć, co jest mitigowane, co jest akceptowanym ryzykiem. Nawet "nic nie opuszcza urządzenia" to valid postura — ale spisana.
**Deliverables:** `docs/06-architecture.md` (overview + diagramy C4); `docs/adr/0001-architecture-style.md`, `0002-modularization.md`, `0003-di-framework.md`, … (4–8 ADR-ów razem); `docs/threat-model.md`
**Źródła:** Simon Brown *Software Architecture for Developers* (C4); Michael Nygard "Documenting Architecture Decisions" (2011); szablon arc42; OWASP SAMM Threat Assessment; framework LINDDUN (KU Leuven).

### Faza 6.5 — Plan Walking Skeleton

**Cel:** Zaplanować end-to-end najcieńszy możliwy vertical slice (jeden ekran → ViewModel → Room → z powrotem) **zanim** wejdziemy w pełny sprint feature'ów. Waliduje, że architektura z Fazy 6 faktycznie się składa.
**Działania:** Wybór najprostszego sensownego wycinka (najpewniej: "Dodaj ćwiczenie do biblioteki, zobacz je na liście"). Definicja co wchodzi, co stub, co odroczone. To *plan*, jeszcze nie kod.
**Deliverable:** `docs/walking-skeleton.md`
**Źródła:** Alistair Cockburn (1999) "Walking Skeleton"; Hunt & Thomas *The Pragmatic Programmer* (tracer bullets).

### Faza 7 — Roadmap, scenariusze jakości i Definition of Done

**Cel:** Plan iteracji + mierzalny próg jakości.
**Działania:**
- Milestone breakdown: M1 (scaffolding + biblioteka ćwiczeń), M2 (CRUD planów), M3 (sesja treningu + LoggedSet), M4 (statystyki), M5 (polish + release na Play).
- **Quality Attribute Scenarios** w formacie arc42 §10: *Source / Stimulus / Artifact / Environment / Response / Response Measure* — dla wydajności (np. "zalogowanie serii < 200ms p95"), niezawodności, użyteczności, bezpieczeństwa, utrzymywalności.
- **Definition of Done** dla feature'ów (testy zielone, brak warningów Detekt, screenshot w PR, ADR jeśli architektoniczne).
**Deliverables:** `docs/07-roadmap.md`, `docs/08-quality.md`

---

## Definition of Done — faza designu

Zanim wejdziemy w scaffolding kodu (Faza 8), wszystkie 4 muszą być true:

1. **Pokrycie:** każda fika MVP z PRD ma odpowiednik w wireframe i co najmniej jednej encji domain modelu.
2. **Kompletność:** każdy ekran ma flow, każda encja ma typowane atrybuty, każda znacząca decyzja ma ADR.
3. **Wykonalność:** MVP jest realnie do zbudowania w zaplanowanych milestone'ach, biorąc pod uwagę solo capacity.
4. **Portfolio-readiness:** `docs/` opowiada historię end-to-end tak, że senior reviewer potrzebuje ≤ 5 minut żeby załapać *co* i *dlaczego*.

Jeśli którekolwiek = false → wracamy do odpowiedniej fazy.

---

## Narzędzia

| Narzędzie | Do czego | Koszt |
|-----------|----------|-------|
| Markdown | Wszystkie artefakty `docs/*.md` | 0 |
| Mermaid | ER, flowy, state machines, OST, C4-lite (renderują się w GitHub) | 0 |
| Figma (free) | Wireframes lo-fi | 0 |
| ADR (Nygard) | Decyzje architektoniczne, konwencja numerowania `adr-tools` (`0001-*.md`) | 0 |
| GitHub Actions | CI: lint + unit tests + instrumented testy na hosted emulator | 0 |
| mkdocs-material | (opcjonalnie) Publikacja `docs/` jako strona na GitHub Pages | 0 |

## Portfolio polish (table stakes 2025)

To nie są deliverables faz, ale są oczekiwane przez senior reviewerów Androida:

- C4 Context + Container w Fazie 6
- ADR-y numerowane `0001-*.md`, `0002-*.md`, … (konwencja `adr-tools`)
- GitHub Actions CI + status badge w README
- Module graph (`./gradlew projectDependencyGraph`)
- Screen recording / GIF w README + APK opublikowany w GitHub Releases
- Conventional Commits + auto-generowany `CHANGELOG.md`

## Jak praktycznie pracujemy

Per faza: (1) ja zadaję pytania sokratejsko, (2) Ty decydujesz / korygujesz, (3) ja draftuję artefakt, (4) review wspólnie, (5) commit + push wg `CLAUDE.md` (dwujęzyczne EN+PL), (6) następna faza. Każda faza to zwykle 2–4 sesje dialogu. Iteracyjnie, nie waterfall.

## Referencje

- Zasady projektu: `CLAUDE.md`
- Output Fazy 1: `docs/01-vision.md`
- Living glossary: `docs/glossary.md`
- ADR-y: `docs/adr/`
