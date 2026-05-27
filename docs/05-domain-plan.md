# Domena — Plan

> TL;DR: Template treningu. 3 encje (TrainingPlan/PlannedDay/PlannedExercise) — mutowalne w miejscu, hard delete cascade. Jeden aktywny plan na raz.

## Spec

### TrainingPlan
| Atrybut | Typ | Constraints |
|---|---|---|
| `id` | `Long` | PK, autoincrement |
| `name` | `String` | non-blank (trim), unique per user |
| `isActive` | `Boolean` | default false |
| `createdAt` | `Instant` | non-null, set on insert |

**Invarianty:**
- `name.trim().isNotEmpty()`, unique globalnie (wśród istniejących rekordów).
- Co najwyżej 1 rekord z `isActive == true` (enforce w repo: transakcja deactivate-all → activate-one).

**Operacje:** `create(name)` · `rename(id, newName)` · `setActive(id)` · `delete(id)` (hard, cascade).

### PlannedDay
| Atrybut | Typ | Constraints |
|---|---|---|
| `id` | `Long` | PK, autoincrement |
| `trainingPlanId` | `Long` | FK → TrainingPlan, ON DELETE CASCADE |
| `name` | `String` | non-blank, unique within plan |
| `order` | `Int` | >=0, unique within plan |

**Invarianty:** `name` non-blank, unique per `trainingPlanId`. `order` definiuje sekwencję dni.

**Operacje:** `create(planId, name)` (append: `order = max+1`) · `rename` · `reorder(id, newOrder)` (re-sequence w transakcji) · `delete(id)` (cascade).

### PlannedExercise
| Atrybut | Typ | Constraints |
|---|---|---|
| `id` | `Long` | PK, autoincrement |
| `plannedDayId` | `Long` | FK → PlannedDay, ON DELETE CASCADE |
| `exerciseId` | `Long` | FK → Exercise, NO ACTION |
| `order` | `Int` | >=0, unique within day |
| `targetReps` | `List<Int>` | size >=1, każdy element >=1 (Room `@TypeConverter`) |
| `restSeconds` | `Int?` | null lub >=0 (sekundy) |

**Invarianty:** `targetReps.isNotEmpty()`, `targetReps.all { it >= 1 }`. `sets` derived = `targetReps.size`. `exerciseId` może wskazywać soft-deleted Exercise — UI edycji pokazuje marker "usunięte", read-only.

**Operacje:** `add(dayId, exerciseId, targetReps, restSeconds?)` (odrzuca soft-deleted Exercise) · `updateParams` · `reorder` · `remove(id)` (hard).

### Aktywny plan — query
`SELECT * FROM TrainingPlan WHERE isActive = 1 LIMIT 1` — start sesji z aktywnego, lub null = user wybiera ręcznie przy starcie.

## Rationale

**Mutowalność w miejscu:** snapshot w Session ratuje historię (cross-cutting F5), brak wersjonowania = mniej encji, mniej query complexity. Wersjonowanie planu = YAGNI na MVP, additywne później.

**Hard delete + cascade:** sesja ma denormalized snapshot (`planNameSnapshot`, `dayNameSnapshot`, ćwiczenia w `SessionExerciseSnapshot` — [[05-domain-session]]) — usuwanie planu nie szkodzi historii. Cascade na PlannedDay/PlannedExercise = brak orphans.

**`exerciseId` NO ACTION (nie SET NULL):** Exercise ma soft delete — referencja zostaje, w UI marker "usunięte". Spójne z `05-domain-exercise.md` (US-2).

**`isActive` boolean vs osobna tabela ActivePlan:** boolean prostszy, invariant "≤1 active" enforced w repo (transakcja). Tabela `ActivePlan` z 1 rzędem = over-engineering.

**`targetReps: List<Int>` per seria zamiast zakresu/single:** wspiera strategię "stała sekwencja reps (np. 10/9/8) → progresja przez ciężar w kolejnej sesji". Zakres `repsMin..repsMax` gubi intencję per-serii, single Int wymusza identyczne reps we wszystkich seriach. Lista przez `@TypeConverter` (Room nie ma natywnego `List<Int>` — serializacja CSV).

**`restSeconds: Int?` per ćwiczenie:** PRD US-5 + USP §5 ("timer w historii kontekstualizuje wyniki") wymagają planowanego czasu — bez celu nie ma czego sygnalizować wibracją. null = brak celu (RestTimer countup bez alertu). Realny czas zawsze derived z `completedAt` ([[05-domain-session]] Rest interval) — `restSeconds` to *cel*, nie *wynik*.

**Brak `targetRpe` w MVP:** YAGNI — RPE pojawia się w sesji po fakcie (US-3), nie pre-skrybujemy intensywności na planie. **Brak `weight` w planie:** US-3 AC = ciężar pre-fill z ostatniej sesji, plan świadomie nie kotwiczy ciężaru (progression sterowana z log historii).