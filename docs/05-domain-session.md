# Domena — Session

> TL;DR: Realizacja treningu. 3 encje (WorkoutSession/SessionExerciseSnapshot/LoggedSet) + enum Rpe. Snapshot ref+denorm — historia odporna na zmiany planu i soft-delete Exercise.

## Spec

### WorkoutSession
| Atrybut | Typ | Constraints |
|---|---|---|
| `id` | `Long` | PK, autoincrement |
| `plannedDayId` | `Long?` | FK → PlannedDay, ON DELETE SET NULL. NOT NULL przy insert. |
| `planNameSnapshot` | `String` | non-blank, immutable po insert |
| `dayNameSnapshot` | `String` | non-blank, immutable po insert |
| `startedAt` | `Instant` | non-null, set on insert |
| `finishedAt` | `Instant?` | null = w trakcie. `>= startedAt` gdy not null. |

**Invarianty:** Co najwyżej 1 rekord z `finishedAt == null` globalnie (enforce w repo). Snapshoty (`planNameSnapshot`, `dayNameSnapshot`) zamrożone w `start()` — nie aktualizują się po rename planu.

**Operacje:** `start(plannedDayId)` (transakcja: snapshot plan/day name + utworzenie `SessionExerciseSnapshot` per PlannedExercise z bieżącego dnia) · `finish(id)` (ustawia `finishedAt = now`) · `delete(id)` (hard, cascade na snapshots i sety).

### SessionExerciseSnapshot
| Atrybut | Typ | Constraints |
|---|---|---|
| `id` | `Long` | PK, autoincrement |
| `sessionId` | `Long` | FK → WorkoutSession, ON DELETE CASCADE |
| `exerciseId` | `Long` | FK → Exercise, NO ACTION |
| `exerciseNameSnapshot` | `String` | non-blank, immutable |
| `muscleGroupSnapshot` | `MuscleGroup` | non-null, immutable |
| `order` | `Int` | >=0, unique within `sessionId` |
| `plannedSets` | `Int` | >=1 |
| `plannedRepsMin` | `Int` | >=1 |
| `plannedRepsMax` | `Int` | >= `plannedRepsMin` |
| `plannedWeight` | `Double?` | null lub >=0 |

**Invarianty:** Immutable po `WorkoutSession.start()` — snapshot z `PlannedExercise` w momencie startu. `exerciseId` może wskazywać soft-deleted Exercise (UI: marker "usunięte", nawigacja read-only).

**Operacje:** Brak publicznego CRUD — tworzony tylko przez `WorkoutSession.start()`, usuwany przez cascade.

### LoggedSet
| Atrybut | Typ | Constraints |
|---|---|---|
| `id` | `Long` | PK, autoincrement |
| `sessionExerciseSnapshotId` | `Long` | FK → SessionExerciseSnapshot, ON DELETE CASCADE |
| `order` | `Int` | >=1, unique within `sessionExerciseSnapshotId` |
| `reps` | `Int` | >=1 |
| `weight` | `Double` | >=0 |
| `rpe` | `Rpe?` | nullable, enum (Easy/Normal/Hard) |
| `completedAt` | `Instant` | non-null, set on insert |

**Invarianty:** `reps >= 1` (zero = nie loguj). `weight >= 0` (0 OK dla bodyweight). `order` definiuje sekwencję serii w ćwiczeniu — re-numerated przy delete.

**Operacje:** `log(snapshotId, reps, weight, rpe?)` (append: `order = max+1`, `completedAt = now`) · `edit(id, reps, weight, rpe?)` · `delete(id)` (hard, re-sequence).

### Rpe enum
`Easy | Normal | Hard` — patrz [`glossary.md#RPE`](glossary.md). Opcjonalne na LoggedSet.

### Rest interval — derived
Rest przed serią = `completedAt[n] - completedAt[n-1]` w obrębie tego samego `SessionExerciseSnapshot`. Pierwsza seria: "—". Nie persistujemy — derive w query.

### 1RM estimate — derived
Epley: `weight × (1 + reps / 30)` per LoggedSet. Best set per Exercise = `MAX(1RM)` w okresie filtra (US-6).

## Rationale

**`plannedDayId` nullable + SET NULL:** Plan można hard-delete ([[05-domain-plan]]), sesja przetrwa dzięki snapshotom nazw. NOT NULL tylko przy insert — egzekwuje decyzję "sesja tylko z planu" w punkcie startu.

**`finishedAt: Instant?` zamiast enum statusu:** Dwa stany (in-progress/completed) wystarczają w MVP. "Abandoned" da się derive heurystyką (>24h bez serii) — explicit enum to YAGNI. Glossary `NotStarted` istnieje przed insertem (sesja nie utworzona) — nie wymaga reprezentacji w schemacie.

**Snapshot pełen (name + muscleGroup + planned params):** Historia 100% samowystarczalna do wyświetlenia. `exerciseId` zachowany dla nawigacji UI ("zobacz w bibliotece") i agregacji cross-session (Last Set Preview, Stats Lite per-Exercise).

**Snapshot immutable:** Edycja planu nie tyka istniejących sesji. Dodanie ćwiczenia do planu w trakcie sesji wpada do następnej, nie tej trwającej. ADR-kandydat na F6.

**Rest derived z `completedAt`:** Jeden source of truth dla czasu. Edycja serii nie wymaga aktualizacji sąsiednich rekordów.

**Rpe opcjonalny:** Core flow logowania = `reps + weight` (US-3 AC). RPE = wartość dodana per-seria, nie blokuje zapisu.

**Cascade na `WorkoutSession.delete`:** Hard delete sesji = user explicit usuwa historię. Brak soft delete — wireframes nie przewidują "kosza" sesji.
