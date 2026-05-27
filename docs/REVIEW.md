# Docs Review — 2026-05-27

## Podsumowanie

Projekt po zamknięciu **Fazy 5 (Domain Model)**, przed Fazą 6. Dokumentacja narracji jest spójna na poziomie scope/IA, ale faza 5 wprowadziła kilka decyzji domenowych (kształt `PlannedExercise`, `isActive` na planie, snapshot immutable, `Rpe?` nullable, `WorkoutSession.finishedAt: Instant?` zamiast enum stanu) **bez propagacji** do PRD / wireframes / glossary. To produkuje 4–5 krytycznych sprzeczności — większość ma jeden wspólny rdzeń: domena ścięła zakres `PlannedExercise` (YAGNI), ale upstream-docs nadal opisują pełniejszy model. Braki sekcji architektury/threat-model/roadmap to **by design** — Faza 6+.

Sekcje techniczne minimalne (Room + Material 3 wzmiankowane bez wersji) — pełna weryfikacja przez context7-mcp odroczona do Fazy 6 gdy pojawi się stack ADR. Sprawdzone próbkowo: M3 `NavigationBar` default container height (claim 80dp w `04-wireframes.md:43` — zgodne ze spec). Założenie o `Room`+IntRange (`05-domain-plan.md:62`) niezweryfikowane bezpośrednio z docs (lookup AndroidX dał noise), ale jest poprawne merytorycznie — `IntRange` nie jest natywnym typem kolumny Room i wymagałby `@TypeConverter`.

## Krytyczne sprzeczności

- **`02-prd.md:34` + `glossary.md:41` + `04-wireframes-crud.md:26` + `04-wireframes-crud.md:13` + `04-wireframes-session.md:8` vs `05-domain-plan.md:34-43,64`** — **Rest Timer per ćwiczenie w planie.**
  - PRD US-2: "parametry (serie, **timer**) per ćwiczenie w planie, nie globalnie".
  - PRD US-5: "auto-start **z planu** po zapisie serii".
  - Glossary: "Konfigurowalny per ćwiczenie w planie".
  - Wireframe M2: `Timer(stepper·min0:30·step0:30)`.
  - Wireframe S5 subtitle: `"N serie·T:00 odpocz."`.
  - Wireframe S9: `wibracja+dźwięk-po-Tplan` (T-plan = wartość z planu).
  - **Domain `PlannedExercise`** explicitnie pomija pole timer (`sets`, `repsMin`, `repsMax`, `weight` — brak `restSeconds`), a rationale (`05-domain-plan.md:64`) tłumaczy: *"Brak `restSeconds`/`targetRpe` w MVP: YAGNI — … rest interval nie blokuje logowania."*
  - **Sugestia:** to decyzja merytoryczna, nie kosmetyka — albo (a) odwróć decyzję domeny i dodaj `restSeconds: Int?` do `PlannedExercise` + zaktualizuj rationale, albo (b) wytnij timer z PRD US-5/US-2, glossary, M2, S5 subtitle i S9 "Tplan" i przerobi US-5 AC ("auto-start z planu" → "auto-start z global default 90s" lub "auto-start z ostatniej sesji"). **Bez wyboru jednej ścieżki Faza 6 nie ma czego scaffoldować.**

- **`04-wireframes-crud.md:26` vs `05-domain-plan.md:34-43`** — **Brak pól w M2 dla atrybutów `PlannedExercise`.**
  - Domain definiuje `repsMin`, `repsMax`, `weight: Double?` jako część `PlannedExercise`.
  - Wireframe M2 ma tylko `Serie(stepper)` + `Timer(stepper)` — żadnego inputu dla zakresu powtórzeń ani sugerowanego ciężaru.
  - PRD US-3 AC mówi "pre-fill: serie z planu, **ciężar z ostatniej sesji**" — co implikuje że ciężar w planie nie istnieje. Spójne z PRD, ale niespójne z domain `PlannedExercise.weight`.
  - **Sugestia:** wyrównać w jedną stronę. Jeśli MVP nie wymaga pre-skrypcji ciężaru i zakresu reps w planie (consistent z PRD US-3) → usuń `repsMin/repsMax/weight` z domain `PlannedExercise`, zostaw `sets` i ew. `restSeconds` (patrz sprzeczność wyżej). Jeśli zostają w domain → dodaj odpowiednie steppery do M2 i fragment AC w US-2.

- **`02-prd.md:11` + `glossary.md:38` vs `05-domain-session.md:47,75`** — **RPE: wymagane czy opcjonalne.**
  - PRD MoSCoW: `"Sesja (reps×ciężar×RPE)"` — RPE jako pierwszorzędny atrybut serii.
  - Glossary: *"**Dodawana przy logowaniu każdej serii w MVP**."*
  - Domain `LoggedSet.rpe: Rpe?` (nullable), rationale: *"Rpe opcjonalny … nie blokuje zapisu."*
  - Wireframe S9: `RPE[chips:Łatwa/Normalna/Ciężka]` — bez explicit "opcjonalne".
  - **Sugestia:** rationale domeny jest mocne (≤3s log = RPE nie może blokować) — popraw PRD i glossary tak, by RPE było "rekomendowane, ale pomijalne", i dodaj do S9 sygnał że chips są optional (np. dim state gdy nie wybrane + dopuszczone Zapisz bez wyboru).

- **`01-vision.md:54` vs `04-wireframes-misc.md:16`** — **Theme MVP.**
  - Vision Non-Goals: *"Light mode / wybór motywu — sztywny dark theme."*
  - Wireframe S11: *"MVP minimal (bez theme toggle — **Material You system default**)"*.
  - Material You = dynamic color z systemu, idzie za jasnym/ciemnym motywem systemowym → de facto pozwala na light mode jeśli system ma light.
  - **Sugestia:** doprecyzować w S11: *"sztywny dark theme (Material 3 dark color scheme, **bez** Material You — wizja §7)"*. Albo świadomie zmienić vision na "follow system" + dodaj ADR.

## Niezgodności z dokumentacją bibliotek

⚠️ Weryfikacja głęboka odroczona — projekt przed Fazą 6, brak deklaracji wersji bibliotek (Compose / Room / Hilt / Navigation). Sprawdzono próbkowo, brak niezgodności:

- **`04-wireframes.md:43`** — claim *"M3 default (80dp→64dp)"* dla bottom nav: zgodne z aktualną spec Material 3 (`NavigationBar` container height = 80dp default, override przez `Modifier.height`).
- **`05-domain-plan.md:62`** — claim *"Room nie mapuje IntRange natywnie (wymaga TypeConverter)"*: poprawne merytorycznie, Room obsługuje natywnie typy prymitywne + `String` + `ByteArray` + ich nullable warianty; `IntRange` wymaga `@TypeConverter`.

Faza 6 powinna otworzyć drugi pełen przebieg z context7-mcp na: Navigation Compose typesafe args (`@Serializable` route classes od 2.8+), Hilt `hiltViewModel()` w Compose, `collectAsStateWithLifecycle`, Room `@Relation` dla agregatów Plan/Session.

## Przestarzałe podejścia

Brak uwag. Dokumentacja nie wzmiankuje XML layouts, `Fragment`, `LiveData`, `SharedPreferences`, `AsyncTask`, MVC/MVP ani innych red-flag patterns z `references/android-standards.md`.

## Braki w dokumentacji

- **Aktywacja planu — brak UI dla `TrainingPlan.isActive`.** Domain (`05-domain-plan.md:11,18`) wprowadza `isActive: Boolean` z invariantem "≤1 active" i operacją `setActive(id)`, ale:
  - Wireframe S2/S4 nie ma toggle/akcji "ustaw jako aktywny".
  - Flow F1/F2/F3 nie wzmiankuje aktywacji.
  - PRD US-2 nie definiuje tej akcji.
  - **Skutek:** domain ma pole którego nikt nie potrafi ustawić z UI. Albo dodaj akcję do S4 (np. checkbox `[ ] Aktywny plan` w TopBar), albo zmień strategię (np. "aktywny = ostatnio używany" → derived, usuń `isActive` z TrainingPlan).

- **Logika "smart suggestion" na S1 — undefined.** Wireframe S1 (`04-wireframes-toplevel.md:9`) ma `SmartCard(plan·dzień·"Zacznij")` a Flow F2 (`03-flows.md:47`) mówi "S1 → historia? → smart card". Nigdzie nie sprecyzowano: który plan? który dzień? (next w aktywnym? based on `WorkoutSession` history? cykl rotacyjny dni?). To gap między IA a domain. **Sugestia:** dodać sekcję "Smart suggestion logic" do PRD US-3 lub do `05-domain-session.md` (np. *"plan = `isActive`, dzień = next po max(`startedAt`) wśród `Completed`, fallback = dzień #1"*).

- **`PRD US-1` "edycja nazwy propaguje wszędzie" vs `05-domain-session.md:23` `exerciseNameSnapshot` immutable.** Tension — niejasne czy historyczne sesje pokazują (a) snapshot name z momentu sesji (immutable), (b) current `Exercise.name`, (c) snapshot tylko gdy Exercise soft-deleted, current gdy aktywny. Domain rationale (`05-domain-session.md:69`) sugeruje (a) ("historia 100% samowystarczalna"), ale PRD US-1 ("propaguje wszędzie") sugeruje (b). **Sugestia:** doprecyzować w `05-domain-session.md` *Rationale* która interpretacja obowiązuje + odpowiednio poprawić PRD US-1 AC ("propaguje w bibliotece i przyszłych sesjach; historyczne sesje zachowują nazwę z momentu sesji").

- **`05-domain-exercise.md:11` + `05-domain-plan.md:11` — "unique per user" w projekcie bez kont.** PRD US-7 + Vision §5 USP: "zero konta w MVP". Brak konceptu `user_id` → "per user" jest puste. **Sugestia:** zamień na "unique globalnie (w aktywnych rekordach)".

## Drobne uwagi

- **`03-flows.md:4`** mówi "5 modali", ale `04-wireframes.md:7-27` inwentaryzuje **6** (dodany `D3 Reset danych`). Aktualizuj flows lub przenieś D3 do osobnej sekcji "dialogi systemowe".
- **`04-wireframes-misc.md:19` vs `05-domain-exercise.md:30`** — copy w dialogu D1 (*"Używane w N planach · M sesjach — historia zostanie zachowana jako read-only"*) różni się od message zaproponowanego w domain (*"Ćwiczenie jest w N planie/planach (lista). Po usunięciu zostanie pominięte w sesjach z tych planów."*). Domain mówi tylko o N planach, wireframe pokazuje N planów + M sesji. **Sugestia:** ujednolic — wariant z wireframe jest bogatszy informacyjnie.
- **`glossary.md:17`** — *"Decyzja 'enum vs tabela referencyjna' odroczona do Fazy 5"* — Faza 5 zamknięta, decyzja zapadła (enum, `05-domain-exercise.md:22`). Zaktualizuj glossary: dopisz "→ enum, zamknięty (F5)".
- **`glossary.md:32`** — *"Ma stan: `NotStarted → InProgress → Completed` (pełne maszynowanie stanu — Faza 5)"* — Faza 5 zdecydowała inaczej (`finishedAt: Instant?` zamiast enum, "NotStarted" istnieje przed insertem). Zaktualizuj glossary.
- **`glossary.md:29`** — *"Ćwiczenie zaplanowane … referencja … + liczba serii + timer odpoczynku"* — kolizja z domain (brak timer w `PlannedExercise`) — patrz pierwsza krytyczna sprzeczność, glossary dryfuje razem z PRD.
- **`04-wireframes-toplevel.md:7`** — S1 oznaczony `[PRD 4.3]`, ale 4.3 to US-3 (Sesja), nie ekran Trenuj. Drobna pomyłka tagu sekcji PRD (PRD nie ma sekcji 4.x explicit, US-3 = sesja). Zostaw lub usuń tag.
- **`04-wireframes-session.md:8`** — `LastSetPreview("reps × kg / RPE — N dni temu" · brak→"Brak poprzedniej sesji")` — jeśli ostatnia seria miała `rpe == null` (per domain decision), format `reps × kg / RPE` nie obsługuje tego edge-case. **Sugestia:** dodać wariant *"… reps × kg — N dni temu"* (bez segmentu RPE) gdy `rpe == null`.
- **`05-domain-session.md:11`** — *"FK → PlannedDay, ON DELETE SET NULL. NOT NULL przy insert."* — sformułowanie "NOT NULL przy insert" jest application-level invariant, nie schema constraint (kolumna jest nullable na poziomie SQL). Doprecyzuj: *"Schema: nullable (SET NULL przy hard-delete PlannedDay). Application invariant: required przy `start()`."*
- Wszystkie pliki `docs/0X-*.md` mieszczą się w limicie 100 linii — OK.

## Ocena ogólna

**Skala 1–10: 7/10**

Dokumentacja narracji (vision → PRD → flows → wireframes) jest **bardzo dobrze poprowadzona** — token-discipline trzyma się, glossary aktywne, decyzje IA mają rationale. Faza 5 zrealizowała szczegółowy domain model z dobrymi trade-off'ami (snapshot, soft-delete tylko Exercise, derived rest interval).

**Co wymaga uwagi w pierwszej kolejności (przed F6):**
1. **Domknij decyzję o Rest Timer w PlannedExercise** — to blokuje 4 inne dokumenty. To merytoryczna decyzja (pre-skrypcja w planie vs derive-only), nie rewrite.
2. **Wyrównać `PlannedExercise` ↔ M2** — albo domain ściąga `repsMin/repsMax/weight`, albo M2 dostaje steppery.
3. **Zdecyduj o `isActive`** + dodaj UI lub wytnij z domain.
4. **Spropaguj decyzje F5 do glossary** — 3 wpisy (MuscleGroup, Workout Session state, PlannedExercise) są outdated.
5. **Dopnij temat RPE** (wymagane vs opcjonalne) — wpływa na S9 UX i AC US-3.

Po tych poprawkach docs będą czysto opowiadać historię end-to-end (DoD pkt 4) i Faza 6 może wystartować bez retro-edytów upstream.
