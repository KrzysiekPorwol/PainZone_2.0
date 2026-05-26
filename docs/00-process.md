

# Proces designu — PainZone 2.0

> Meta-dokument: **jak** projektujemy tę apkę przed napisaniem kodu.
> Outputy faz mieszkają w `docs/01-vision.md` … `docs/07-roadmap.md`.
> Ten plik to żywy referens — ewoluuje, gdy uznamy że proces powinien się zmienić.

## Status

| Faza                                | Deliverable | Status |
|-------------------------------------|-------------|--------|
| 0. Setup kontraktu projektu         | `CLAUDE.md` (live state, reguły współpracy) | 🟢 done |
| 1. Vision & Discovery               | `docs/01-vision.md` | 🟢 done |
| 2. PRD + OST                        | `docs/02-prd.md` | 🟢 done |
| 3. User Flows + IA                  | `docs/03-flows.md` | 🟢 done |
| 4. Wireframes (Lo-Fi)               | `docs/04-wireframes.md` + link do Figmy | 🟡 w toku |
| 5. Domain Model                     | `docs/05-domain.md` | ⏳ przed nami |
| 6. Architektura + ADR + Threat Model | `docs/06-architecture.md`, `docs/adr/*`, `docs/threat-model.md` | ⏳ przed nami |
| 6.5. Walking Skeleton               | `docs/walking-skeleton.md` | ⏳ przed nami |
| 7. Roadmap + scenariusze jakości    | `docs/07-roadmap.md`, `docs/08-quality.md` | ⏳ przed nami |

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

**Cel:** Ustawienie `CLAUDE.md` jako always-loaded kontraktu — każda przyszła sesja startuje z tą samą orientacją: kontekst projektu, styl współpracy, decision authority.
**Działania:** Dodać do `CLAUDE.md` sekcje użyteczne w *każdej* sesji. Każda sekcja musi zarabiać miejsce — ciężka/specyficzna dla fazy zawartość zostaje w `docs/`.
**Deliverable:** Zaktualizowany `CLAUDE.md` (~70 linii razem, mocno poniżej strefy ~200 linii).
**Źródła:** dokumentacja Claude Code Anthropic (konwencja CLAUDE.md); literatura team agreements (Lyssa Adkins *Coaching Agile Teams*).

### Faza 1 — Vision & Discovery

**Cel:** Jednozdaniowe odpowiedzi: co to, dla kogo, dlaczego, metryka sukcesu, non-goals.
**Działania:** Wywiad w stylu Lean Canvas / Vision Statement, persona, problem, USP, metryki sukcesu, non-goals. Start `glossary.md` od pierwszych ~10 terminów domenowych.
**Deliverable:** `docs/01-vision.md`
**Źródła:** Eric Ries *The Lean Startup*; Ash Maurya *Running Lean* (Lean Canvas); Jonathan Rasmusson *The Agile Samurai* (Inception Deck).

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

