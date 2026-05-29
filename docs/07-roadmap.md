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
- **M2.8 · Aktywacja planu** — ⭐ toggle, ≤1 aktywny enforced, SmartCard placeholder na Trenuj.
- (zrobione) **M2.9 · Usuwanie planu z listy** — kosz w wierszu `PlansScreen` + dialog potwierdzenia, hard delete (`PlanRepository.delete`, FK CASCADE na dni/ćwiczenia), snackbar „Usunięto". Bez ostrzeżenia o aktywnym planie (decyzja: aktywacją zarządza M2.8).

### M3 · Session (US-3, US-4, US-5)
Sesja treningu — log ≤3s, Last Set Preview inline, Rest Timer.
- **M3.1 · WorkoutSession + LoggedSet encje** — snapshot pattern (ADR-0003) + invarianty (`05-domain-session.md`) + unit testy.
- **M3.2 · Session Room layer** — `SessionEntity` + `LoggedSetEntity` + DAO + migration v3.
- **M3.3 · SessionRepository** — start z planu z pre-fill (cel z planu, ciężar z ostatniej sesji), pauza/wznowienie.
- **M3.4 · SessionScreen szkielet** — lista ćwiczeń z planu, aktywne ćwiczenie, nav między ćwiczeniami.
- **M3.5 · Log seria UX** — input reps × ciężar × RPE chips, save ≤3s (PRD A2), auto-fokus, edycja świeżej serii nadpisuje.
- **M3.6 · Last Set Preview** — inline „reps × ciężar / RPE — N dni temu" lub „Brak poprzedniej sesji", chronologicznie ostatnia.
- **M3.7 · Rest Timer** — auto-start po zapisie serii (`restSeconds` z planu), `restBeforeSeconds=actual` w kolejnym `LoggedSet`.
- **M3.8 · Timer overflow** — wibracja + dźwięk na przekroczenie targetu, timer dalej liczy.
- **M3.9 · Pauza 30min recovery** — `InProgress` state restore po killowaniu procesu (Room + lifecycle).
- **M3.10 · Zakończ sesję** — transition do `Completed`, read-only enforced.

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
