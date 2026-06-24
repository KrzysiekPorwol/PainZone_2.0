# Roadmap — PainZone 2.0

> TL;DR: 6 milestones (M0–M5), atomic tasks. 1 task = 1 sesja Claude. Bez deadline'ów, jakość > tempo.

## Spec

### Zasady
- 1 task = 1 sesja Claude (skończ → commit → push → `/clear`). Workflow: `docs/rules.md`.
- Milestone zamknięty = wszystkie tasks done + manual smoke test golden path E2E (skill `verify`).
- Post-release roadmap (SHOULD/COULD z PRD `02-prd.md`) — osobny plik po release MVP.

### M0 · Walking Skeleton
Stack boot + 1 placeholder ekran end-to-end.
- (zrobione) **M0.1 · Project bootstrap** — Gradle: Compose BOM 2026.03, minSdk 26, Kotlin + KSP + `kotlinx.serialization` plugins, deps z `06-architecture.md#Stack`.
- (zrobione) **M0.2 · Hilt setup** — `PainZoneApp @HiltAndroidApp`, `MainActivity @AndroidEntryPoint`, hilt-android-gradle-plugin.
- (zrobione) **M0.3 · Compose theme** — `PainZoneTheme` sticky dark Material 3 (ADR-0005), `Color`/`Type` w `ui/theme/`.
- (zrobione) **M0.4 · NavGraph + bottom bar** — `Routes` (@Serializable, ADR-0001) + `PainZoneNavHost` + Material 3 `NavigationBar` (Wariant B+ z `04-wireframes.md`).
- (zrobione) **M0.5 · Room boot** — `PainZoneDatabase` v1 + `Converters` (`Instant`/`MuscleGroup`/`Rpe`) + `DatabaseModule` Hilt. Minimalna `ExerciseEntity` (placeholder dla Room compiler — schema 1 entity, DAO/mapper w M1.2). Schema export `app/schemas/`.

### M1 · Library (US-1)
Biblioteka ćwiczeń CRUD z soft delete.
- (zrobione) **M1.1 · Exercise encja** — Pure Kotlin domain class + `MuscleGroup` (decyzja F5) + invarianty (`05-domain-exercise.md`) + unit testy.
- (zrobione) **M1.2 · Exercise Room layer** — `ExerciseEntity` + `ExerciseDao` + mapper Entity↔Domain + migration v1.
- (zrobione) **M1.3 · ExerciseRepository** — interfejs w `domain/`, impl w `data/`, Hilt binding, soft delete logic.
- (zrobione) **M1.4 · LibraryScreen list + empty** — S7 (`04-wireframes-library.md`) — Compose + VM + `StateFlow` + `collectAsStateWithLifecycle` + previews. Dostępny przez menu ⋮ z top-level (S1/S2/S3 wg `04-wireframes-toplevel.md`) — **brak w bottom barze**. Dodanie nowego ćwiczenia ma też drugą ścieżkę z S6 picker w trakcie budowania planu (M3 modal, scope: M2).
- (zrobione) **M1.5 · LibraryAddEditModal** — bottom sheet formularz (tryb Add), walidacja real-time, save disabled bez wymaganych pól, error inline dla DuplicateName. Tryb Edit odłożony do M1.7.
- (zrobione) **M1.6 · LibraryDeleteWarningDialog** — dialog z counterem (placeholder 0/0 do M2.3/M3.3), soft delete on confirm + snackbar „Usunięto", trailing ikona kosza w wierszu listy.
- (zrobione) **M1.7 · Edycja Exercise — propagacja** — tap na wiersz → S8 modal Edit (tylko nazwa, MuscleGroup read-only), back-dirty dialog „Odrzucić zmiany?", UsageInfo placeholder 0/0. Test propagacji `rename → observeActive` zielony; test snapshot-immutability odłożony do M3.2 (gdy `SessionExerciseSnapshot` istnieje).

### M2 · Plans (US-2)
Plany treningowe — dni × ćwiczenia z parametrami, ≤1 aktywny.
- (zrobione) **M2.1 · Plan/PlanDay/PlanItem encje** — `TrainingPlan`/`PlannedDay`/`PlannedExercise` pure Kotlin (domain/plan/) + invarianty (name trimmed/non-blank, `order>=0`, `targetReps` non-empty+`>=1`, `restSeconds` null|`>=0`, `sets` derived) + unit testy. ≤1 aktywny i uniqueness odłożone do `PlanRepository` (M2.3).
- (zrobione) **M2.2 · Plan Room layer** — `TrainingPlanEntity` + `PlannedDayEntity` + `PlannedExerciseEntity` + 3 DAO + `PlanWithDays`/`DayWithExercises` `@Relation` POJOs + `MIGRATION_1_2` (schema v2). `List<Int>` converter (CSV) dla `targetReps`. FK `exercise_id` ON DELETE NO ACTION (soft-delete Exercise zostawia referencję).
- (zrobione) **M2.3 · PlanRepository** — interfejs w `domain/plan/` + impl w `data/plan/` + Hilt binding. Sealed result types per operacja (Plan/Day/Exercise). Atomowa aktywacja przez `@Transaction activateExclusive(id)` w `TrainingPlanDao` (deactivateAll + activateById). `addExercise` odrzuca soft-deleted Exercise (`ExerciseDeleted`). Reorder = prosty `update(order)`, pełny re-sequence odłożony do M2.7. `ExerciseRepository.getUsageCount.plansCount` podłączony przez `PlannedExerciseDao.countDistinctPlansForExercise` (sessionsCount=0 do M3.3).
- (zrobione) **M2.4 · PlansScreen list + empty** — Compose + VM + previews. Karta planu: nazwa + „N dni" + ⭐ aktywny (projekcja `PlanSummary`/`observeSummaries` COUNT dni, bez N+1). „ostatnio Xd" odłożone do M3 (sesje). Nawigacja do S4 (`onCreatePlan`/`onOpenPlan`) no-op do M2.5/M2.6.
- (zrobione) **M2.5 · PlanCreateScreen** — formularz nazwa + dni (kolejność zachowana). Bufor w VM, zapis do bazy dopiero na ✓ (`create` → `addDay` w kolejności listy), back-dirty → „Odrzucić zmiany?". Dodawanie dnia przez dialog (walidacja niepuste+unikalne). UI labels „sesja treningowa" zamiast „dzień" (domena/kod dalej `PlannedDay`).
- (zrobione) **M2.6 · PlanDetailScreen** — dodawanie itemów per dzień, parametry per item (target reps, rest).
- (zrobione) **M2.7 · Reorder ćwiczeń** — drag&drop przez bibliotekę `sh.calvin.reorderable` (ADR-0007), uchwyt ↕ obok kosza. Repo `reorderExercises(dayId, orderedIds)` przepisuje `order=0..n` atomowo (`@Transaction reorderInDay`). „Efekt od następnej sesji" = naturalny (plan mutable + snapshot w sesji, M3).
- (zrobione) **M2.8 · Aktywacja planu** — ⭐ toggle na liście (S2) i w PlanDetail (S4), wspólny `ActivationConfirmDialog` + czysta `activationDecision`. Confirm tylko przy zastąpieniu innego aktywnego planu; pierwsza aktywacja i deaktywacja bez pytania. `PlanRepository.deactivate(id)` (komplement do ekskluzywnego `setActive`), invariant ≤1 trzyma baza. SmartCard placeholder na Trenuj (S1): aktywny plan → karta + „Zacznij" disabled (do M3); brak aktywnego → CTA „Przejdź do planów".
- (zrobione) **M2.9 · Usuwanie planu z listy** — kosz w wierszu `PlansScreen` + dialog potwierdzenia, hard delete (`PlanRepository.delete`, FK CASCADE na dni/ćwiczenia), snackbar „Usunięto". Bez ostrzeżenia o aktywnym planie (decyzja: aktywacją zarządza M2.8).

### M3 · Session (US-3, US-4, US-5)
Sesja treningu — log ≤3s, Last Set Preview inline, Rest Timer.
- (zrobione) **M3.1 · WorkoutSession + LoggedSet encje** — `WorkoutSession`/`SessionExerciseSnapshot`/`LoggedSet` pure Kotlin (domain/session/) + invarianty (snapshot names trimmed/non-blank, `finishedAt>=startedAt`, `order>=0`/`>=1`, `reps>=1`, `weight>=0`, `plannedTargetReps` non-empty+`>=1`) + unit testy. `finish()` idempotentny (guard stanu → repo M3.10). „≤1 in-progress globalnie" + tworzenie snapshotów odłożone do `SessionRepository` (M3.3).
- (zrobione) **M3.2 · Session Room layer** — `WorkoutSessionEntity` (FK→day `ON DELETE SET NULL`) + `SessionExerciseSnapshotEntity` (FK→session CASCADE, FK→exercise NO ACTION) + `LoggedSetEntity` (FK→snapshot CASCADE) + mappery + 3 DAO + 2 relation POJO (`SessionWithSnapshots`/`SnapshotWithLoggedSets`) + `MIGRATION_2_3` (schema v3). `insertAll` snapshotów pod `start()`, `resequence` setów po delete. Derived queries (Last Set Preview, smart suggestion) odłożone do M3.3/M4.
- (zrobione) **M3.3 · SessionRepository** — interfejs w `domain/session/` + impl w `data/session/` + Hilt binding. `start(plannedDayId)` atomowo (`@Transaction startWithSnapshots` w `WorkoutSessionDao`): snapshot nazw planu/dnia + `SessionExerciseSnapshot` per `PlannedExercise` (cel z planu); egzekwuje ≤1 sesji w toku (`AlreadyInProgress`) + odrzuca dzień bez ćwiczeń (`EmptyDay`). Soft-deleted Exercise snapshotowany (zamrożona nazwa). `observeInProgress`/`SessionDetail` (POJO `SessionWithDetail`, zagnieżdżony `@Relation`) do pauzy/wznowienia. `lastWeightForExercise` = pre-fill ciężaru. `finish`/`log`/`edit`/`delete` odłożone do M3.5/M3.10. Bez migracji (schema v3 bez zmian).
- (zrobione) **M3.4 · SessionScreen szkielet** — route `Session(sessionId)` (S9 focus mode, bottom bar ukryty) + `SessionScreen`/VM/UiState renderujące snapshot planu/dnia, aktywne ćwiczenie i nav: bottom sheet „skocz do ćwiczenia" („Ćw N/M") + przyciski Poprzednie/Następne (`activeIndex` w VM). Wejście: karta Trenuj rozdziela banner „Sesja w toku" (własny snapshot sesji) od karty aktywnego planu — „Zacznij" startuje sesję na 1. dniu (MIN order), zablokowany gdy trwa sesja; „Wznów" wraca do właściwej sesji niezależnie od zmian aktywnego planu. Minimalny `SessionRepository.finish` + „Zakończ sesję" (menu ⋮ i ostatnie ćwiczenie) dostępne w połowie planu z dialogiem. Pełna smart suggestion (rotacja dnia wg MAX startedAt), Last Set Preview, log input, rest timer i pełne D2/read-only odłożone do M3.5–M3.10.
- (zrobione) **M3.5 · Log seria UX** — input reps × ciężar (stepper 0.5) × RPE chips, save ≤3s (PRD A2), auto-fokus reps po zapisie, tap świeżej serii → nadpisanie (`SessionRepository.log/edit`). Prefill reps z celu planu, ciężar przenoszony z ostatniej serii/sesji. Komplet serii → input zamienia się w CTA „Następne ćwiczenie"/„Zakończ sesję" (blokuje logowanie ponad plan). `delete` serii odłożony do M3.10.
- (zrobione) **M3.6 · Last Set Preview** — inline per-seria: dla serii K „Ostatnio: reps × ciężar / RPE — N dni temu" z serii K ostatniej poprzedniej sesji tego ćwiczenia; brak poprzedniej sesji → „Tym planem trenujesz 1 raz."
- (zrobione) **M3.7 · Rest Timer** — auto-start po zapisie serii (`restSeconds` z planu), `restBeforeSeconds=actual` w kolejnym `LoggedSet`. Banner count-up liczony z `completedAt` ostatniej serii (auto-reset + odporny na śmierć procesu, przygotowuje M3.9). Rest persistowany, nie derived (ADR-0008, schema v4).
- (zrobione) **M3.8 · Timer overflow** — jednorazowa wibracja + dźwięk na przekroczenie `restSeconds`, timer liczy dalej. Detekcja przejścia pod→nad na żywo per `lastSetId` (`SessionViewModel.observeRestOverflow`) → alert dokładnie raz na odpoczynek; wznowienie do już-przekroczonego restu nie buczy (przygotowuje M3.9). Wibracja/dźwięk jako efekt platformowy poza VM (`RestAlert.kt` + `restOverflow` event). Uprawnienie `VIBRATE`.
- (zrobione) **M3.9 · Pauza 30min recovery** — `InProgress` state restore po killowaniu procesu. `activeIndex` przeniesiony do `SavedStateHandle` (jedyny stan ginący przy śmierci procesu — serie trzyma Room, timer liczy z `completedAt`), więc wznowiona sesja wraca na ćwiczenie, które user zostawił. **Decyzja produktowa:** brak progu „30 min idle → paused" — timer odpoczynku leci bez limitu (zgodne z UX: realnie długa przerwa ma dalej liczyć). Zweryfikowane `am kill` + restart.
- (zrobione) **M3.10 · Zakończ sesję** — D2 dialog z podsumowaniem („N serii · czas Y · tonaż Z kg" + „Niezakończone ćwiczenia: K" gdy K>0) + transition do `Completed`. Read-only wymuszone w repo: `log`/`edit` odrzucają zakończoną sesję (guardy `isSnapshotInProgress`/`isSetInProgress` w `WorkoutSessionDao`). Czas trwania zamrażany w momencie otwarcia dialogu. Read-only view zakończonej sesji (S9 reopen) → M5.2.

### M4 · Stats Lite (US-6)
Historia per ćwiczenie z filtrami i best set.
- **M4.1 · StatsRepository** — query `LoggedSet` per `Exercise` z filtrami (30d/90d/rok/all).
- **M4.2 · 1RM est. formula** — Epley vs Brzycki (decyzja na start, w razie sporu → ADR), pure function w `domain/`, unit testy.
- **M4.3 · StatsExerciseScreen** — lista 90d default + filtr chips, re-render <200ms (NFR z `08-quality.md`).
- **M4.4 · Best set highlight** — najwyższy 1RM est. w aktualnym filtrze wyróżniony.
- **M4.5 · Soft-deleted Exercise w historii** — marker „usunięte", read-only.

### M5 · Polish & Release
History, Settings, ikona, Play Store Internal testing.
- **M5.1 · HistoryScreen** — lista `Completed` sesji + click → read-only detail.
- **M5.2 · SessionDetailScreen** — read-only widok zakończonej sesji (snapshot z momentu sesji).
- **M5.3 · SettingsScreen** — about, version, link do Play Store.
- **M5.4 · App icon** — adaptive icon (foreground + background) + Play Store icon.
- **M5.5 · Play Store assets** — screenshoty, opis (PL), feature graphic, kategoria.
- **M5.6 · Release build** — signing config, R8/proguard rules, Internal testing track upload.
