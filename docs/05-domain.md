# Domain Model — PainZone 2.0

> TL;DR: 7 encji w 3 agregatach (Exercise, Plan, Session). Soft delete tylko na Exercise, snapshot historii w Session. Szczegóły w `05-domain-*.md`.

## Spec

### Moduły

| Moduł | Encje | Status | Plik |
|-------|-------|--------|------|
| Exercise | `Exercise`, `MuscleGroup` (enum) | ✅ | [`05-domain-exercise.md`](05-domain-exercise.md) |
| Plan | `TrainingPlan`, `PlannedDay`, `PlannedExercise` | ⏳ | `05-domain-plan.md` |
| Session | `WorkoutSession`, `SessionExerciseSnapshot`, `LoggedSet`, `Rpe` (enum) | ⏳ | `05-domain-session.md` |

### Cross-cutting decyzje (przyjęte na starcie F5)

- **ID strategy:** wszystkie encje `Long` autoincrement (PK).
- **Timestamps:** `Instant` (UTC). `createdAt` gdzie potrzebne; `deletedAt` tylko Exercise.
- **Soft delete:** tylko Exercise (historia LoggedSet musi przetrwać). Pozostałe — hard delete.
- **Snapshot historii:** `WorkoutSession` kopiuje parametry z `PlannedDay` przy starcie (`SessionExerciseSnapshot`). Edycja planu nie tyka istniejących sesji. Kandydat na ADR w F6.
- **1RM formula (Stats Lite):** Epley — `weight × (1 + reps / 30)`.
- **MuscleGroup:** enum zamknięty, nie tabela referencyjna.

### Agregaty

- **Exercise aggregate:** `Exercise` (root) + `MuscleGroup`.
- **Plan aggregate:** `TrainingPlan` (root) → `PlannedDay` → `PlannedExercise`. `PlannedExercise.exerciseId` → ref do Exercise.
- **Session aggregate:** `WorkoutSession` (root) → `SessionExerciseSnapshot` → `LoggedSet`. Oba trzymają `exerciseId` → ref do Exercise.

Operacje cross-aggregate przez ID, nie nawigację obiektową (DDD-style).

## Rationale

[on-demand — szczegóły dlaczego per encja w plikach modułów]