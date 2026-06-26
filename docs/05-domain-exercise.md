# Domena — Exercise

> TL;DR: Atomowa jednostka biblioteki. Soft delete z pre-delete validation, MuscleGroup jako enum.

## Spec

### Exercise
| Atrybut | Typ | Constraints |
|---------|-----|-------------|
| `id` | `Long` | PK, autoincrement |
| `name` | `String` | non-blank (trim), **unique globalnie** wśród aktywnych (MVP bez kont — patrz Rationale) |
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
- Query: N = lista nazw planów (distinct) z aktywnym `PlannedExercise` z `exerciseId == id`; M = liczba `SessionExerciseSnapshot` (cała historia).
- **Jeśli N niepuste → blokada.** Dialog informacyjny (D1, [[04-wireframes-misc]]): *"Nie można usunąć „X" — używane w planach: …. Usuń je z tych planów, aby móc usunąć z biblioteki."* Jedyna akcja: zamknij. `softDelete` **nie** następuje.
- Jeśli N puste a `M > 0` → confirm z notą *"Historia M sesji zostanie zachowana jako read-only."* User confirms → `softDelete`. Cancel → noop.
- Jeśli N puste i `M == 0` → prosty confirm.

**Relacje (out):**
- 1..N `PlannedExercise` (przez `exerciseId`)
- 1..N `SessionExerciseSnapshot` (przez `exerciseId`)
- 1..N `LoggedSet` (przez `exerciseId`)

**Widoczność soft-deleted:**
- Biblioteka (US-1): filtr `deletedAt IS NULL`.
- Plan edit (US-2): nie da się dodać. Blokada usuwania (wyżej) gwarantuje, że aktywny plan nie zyska nowego ducha; marker "usunięte" zostaje jako fallback dla danych sprzed reguły blokady.
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

**"unique globalnie" w MVP bez kont:** Vision §5 + US-7 = zero kont, brak `user_id` w schemacie. "per user" byłoby pustym kwalifikatorem — w praktyce unique wśród wszystkich aktywnych rekordów na urządzeniu. Migracja do "per user" trywialna gdyby v1.1 wprowadziło konta.

**Soft delete zamiast hard:** historia LoggedSet musi przetrwać usunięcie (US-6: Stats pokazuje "usunięte" ćwiczenia read-only). Hard delete złamałby invariant `LoggedSet.exerciseId != null` lub wymagał denormalizacji nazwy do każdego LoggedSet.

**Skip soft-deleted w sesji (model a):** USP "logowanie bez ceremonii" — sesja w drzwiach siłowni nie może blokować się modalami. Skip pozostaje obroną dla danych sprzed blokady (legacy ducha) oraz dla planu, z którego ćwiczenie usunięto po starcie sesji. Read-only marker w sesji (model b) lub blokada (model c) wprowadzają friction w hot path.

**Blokada usuwania gdy w planie (zamiast soft-delete z duchem):** soft-deleted ćwiczenie zostawione w aktywnym planie pokazuje "ducha" („Ćwiczenie usunięte"), który wisi i wygląda na zepsuty. Historia żyje przez `SessionExerciseSnapshot` (zamrożona kopia) — niezależna od żywego FK planu — więc blokada na wymiarze planu **nie** zagraża historii. Decyzja: lepiej zmusić usera do świadomego usunięcia z planów (kontrola nad zawartością planu) niż cicho mutować plan lub trzymać ducha. Soft delete pozostaje dla wymiaru historii (M > 0).

**MuscleGroup enum vs tabela:** lista zamknięta domenowo (anatomia człowieka się nie zmienia), zero user-defined → enum prostszy, type-safe, brak dodatkowych zapytań. Migracja do tabeli możliwa gdyby kiedyś v2 wymagała user-defined kategorii.