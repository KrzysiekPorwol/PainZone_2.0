# Domena — Exercise

> TL;DR: Atomowa jednostka biblioteki. Soft delete z pre-delete validation, MuscleGroup jako enum.

## Spec

### Exercise
| Atrybut | Typ | Constraints |
|---------|-----|-------------|
| `id` | `Long` | PK, autoincrement |
| `name` | `String` | non-blank (trim), **unique per user** wśród aktywnych |
| `muscleGroup` | `MuscleGroup` | non-null (enum) |
| `createdAt` | `Instant` | non-null, set on insert |
| `deletedAt` | `Instant?` | null = active, non-null = soft-deleted |

**Invarianty:**
- `name.trim().isNotEmpty()`, unique wśród `deletedAt IS NULL`.
- `muscleGroup != null` — bez "uncategorized".
- `deletedAt == null || deletedAt >= createdAt`.
- Edycja `name` propaguje globalnie (jeden rekord, brak denormalizacji).

**Operacje:**
- `create(name, muscleGroup)` → nowy Exercise, active.
- `rename(id, newName)` → update (zachowuje wszystkie LoggedSet).
- `softDelete(id)` → set `deletedAt = now()`. Wymaga pre-delete validation (niżej).
- `restore(id)` → set `deletedAt = null` (post-MVP).

**Pre-delete validation (US-1 AC):**
- Query: liczba aktywnych `PlannedExercise` z `exerciseId == id`.
- Jeśli > 0 → UI confirm: *"Ćwiczenie jest w N planie/planach (lista). Po usunięciu zostanie pominięte w sesjach z tych planów. Kontynuować?"*
- User confirms → `softDelete` proceeds. User cancels → noop.

**Relacje (out):**
- 1..N `PlannedExercise` (przez `exerciseId`)
- 1..N `SessionExerciseSnapshot` (przez `exerciseId`)
- 1..N `LoggedSet` (przez `exerciseId`)

**Widoczność soft-deleted:**
- Biblioteka (US-1): filtr `deletedAt IS NULL`.
- Plan edit (US-2): nie da się dodać; istniejące zostają z markerem "usunięte".
- Sesja (US-3): **skip** — pomijane przy starcie z planu (model (a), patrz Rationale).
- Stats Lite (US-6): pokazuje historyczne LoggedSet, marker "usunięte".

### MuscleGroup (enum)
```kotlin
enum class MuscleGroup { Chest, Back, Legs, Biceps, Triceps, Shoulders, Abs }
```
Lista zamknięta. Lokalizacja PL w UI (mapa enum → string resource). Rozszerzenie wymaga zmiany kodu — backward-compatible.

### ExerciseLibrary
Nie encja — query view: `SELECT * FROM exercise WHERE deletedAt IS NULL ORDER BY name`. Startuje pusta (USP §5).

## Rationale

**Soft delete zamiast hard:** historia LoggedSet musi przetrwać usunięcie (US-6: Stats pokazuje "usunięte" ćwiczenia read-only). Hard delete złamałby invariant `LoggedSet.exerciseId != null` lub wymagał denormalizacji nazwy do każdego LoggedSet.

**Skip soft-deleted w sesji (model a):** USP "logowanie bez ceremonii" — sesja w drzwiach siłowni nie może blokować się modalami. Pre-delete validation odbywa się **wcześniej** (przy delete), więc user świadomie zaakceptował konsekwencje. Read-only marker w sesji (model b) lub blokada (model c) wprowadzają friction w hot path.

**MuscleGroup enum vs tabela:** lista zamknięta domenowo (anatomia człowieka się nie zmienia), zero user-defined → enum prostszy, type-safe, brak dodatkowych zapytań. Migracja do tabeli możliwa gdyby kiedyś v2 wymagała user-defined kategorii.