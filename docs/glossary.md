# Glossary — PainZone 2.0

> Ubiquitous language projektu. Lista terminów domenowych których konsekwentnie używamy w **rozmowach, commitach, dokumentach, kodzie (identyfikatory po angielsku)**.
>
> Start: Faza 2 (przy okazji PRD). Promocja do "mature" — Faza 5 (Domain Model): każdy termin z pełną definicją, atrybutami, przykładem, edge case'ami.
>
> Aktualizacje: każda nowa istotna decyzja → tu jako termin. Każdy termin zmieniony przez ADR → tu zaktualizowany.

---

## Terminy v1 — z wizji i PRD (Faza 1-2)

### Ćwiczenie (Exercise)
Atomowa jednostka biblioteki. Reprezentuje "co" trenujemy. Atrybuty: nazwa (np. "Wyciskanie na ławce poziomej") + grupa mięśniowa. **Nie** ma liczby serii, ciężaru, ani timera — te informacje należą do *planu* lub *sesji*, nie do definicji ćwiczenia. **EN w kodzie:** `Exercise`.

### Grupa mięśniowa (MuscleGroup)
Kategoria do której należy ćwiczenie (klatka, plecy, nogi, biceps, triceps, barki, brzuch). Każde ćwiczenie ma dokładnie jedną. **Decyzja F5: enum** (lista zamknięta, type-safe, brak user-defined — `05-domain-exercise.md`). **EN w kodzie:** `MuscleGroup`.

### Biblioteka ćwiczeń (Exercise Library)
Zbiór wszystkich ćwiczeń użytkownika. **Startuje pusta** — user dodaje wyłącznie te których realnie używa (jeden z filarów USP z wizji §5). **EN w kodzie:** `ExerciseLibrary`.

### Plan treningowy (Training Plan)
Szablon treningu. Uporządkowana lista dni → każdy dzień to uporządkowana lista ćwiczeń z parametrami wykonania (docelowe powtórzenia per seria, timer odpoczynku). Plan jest *szablonem* — nie zawiera wyników, tylko intencję. Może być **aktywny** (`isActive`, ≤1 na raz) — aktywny karmi SmartCard w ekranie Trenuj. **EN w kodzie:** `TrainingPlan`.

### Dzień planu (Plan Day / PlannedDay)
Jeden trening w planie (np. "Push", "Pull", "Nogi"). Część planu, nie samodzielny byt. **EN w kodzie:** `PlannedDay`.

### Ćwiczenie zaplanowane (Planned Exercise)
Wpis w dniu planu: referencja do ćwiczenia z biblioteki + lista docelowych powtórzeń per seria (np. `[10, 9, 8]` — sekwencja zamiast zakresu, strategia progresji przez ciężar) + opcjonalny timer odpoczynku **dla tego konkretnego użycia w tym konkretnym planie**. Bez ciężaru — ciężar prefilluje się z ostatniej sesji (US-3). **EN w kodzie:** `PlannedExercise`.

### Sesja treningowa (Workout Session)
Pojedyncza realizacja jednego dnia planu — *konkretne wykonanie* na siłowni, z datą, zarejestrowanymi seriami, rzeczywistym czasem odpoczynku. **Decyzja F5:** stan reprezentowany przez `finishedAt: Instant?` (null = in-progress, non-null = completed); `NotStarted` to przed-stan (sesja jeszcze nieutworzona, brak rekordu). Pełny enum statusu wycięty jako YAGNI — `05-domain-session.md`. **EN w kodzie:** `WorkoutSession`.

### Seria (Set / LoggedSet)
Pojedyncza zarejestrowana w sesji seria: `reps × ciężar × RPE`. **EN w kodzie:** `LoggedSet`.

### RPE (Rate of Perceived Exertion)
Subiektywna trudność serii. W PainZone uproszczona do **3 stopni: łatwa / normalna / ciężka**. **Rekomendowane, ale pomijalne** przy logowaniu serii — `Rpe?` w domain (`05-domain-session.md`), aby nie blokować celu ≤3s/seria (PRD A2). Trzeci stopień ("ciężka") sygnalizuje że user był blisko/na limicie. **EN w kodzie:** `Rpe` (enum: `Easy`, `Normal`, `Hard`).

### Timer odpoczynku (Rest Timer)
Odliczanie aktywne między seriami w trakcie sesji. **Konfigurowalny per ćwiczenie w planie** (każde ćwiczenie ma własny domyślny czas). Rzeczywisty czas odpoczynku (kiedy user kliknął "kolejna seria") **zapisywany do historii** — kontekstualizuje wyniki, killer-detal z wizji §5. **EN w kodzie:** `RestTimer`.

### "Ile było ostatnio" (Last Set Preview)
Killer feature z wizji §5. W trakcie wykonywania serii w sesji, inline obok pól input, widoczna ostatnia zarejestrowana seria dla *tego samego ćwiczenia* z poprzedniej sesji (reps × ciężar × RPE). Pozwala szybko ocenić "czy idę w górę". **EN w kodzie:** `LastSetPreview`.

### Stats Lite
Wersja statystyk w MVP. Ekran per-ćwiczenie: chronologiczna lista wszystkich `LoggedSet` z ostatnich **90 dni domyślnie** + filtr okresu. Best set highlighted. **Bez wykresów** — wykresy progresu per grupa mięśniowa to v1.1 (full Stats). **EN w kodzie:** `StatsLite`.

---

## Referencje

- Wizja: `docs/01-vision.md` (sekcje §3 Persona, §4 Use Cases, §5 USP)
- PRD: `docs/02-prd.md` (MoSCoW §2)
- Proces: `docs/00-process.md` (artefakt żywy — `glossary.md`)
