# ADR-0003: Snapshot pattern w `WorkoutSession`

> TL;DR: Sesja kopiuje parametry z planu i nazwę ćwiczenia przy starcie — historia immutable.

## Kontekst

Edycja `TrainingPlan`/`PlannedExercise`/`Exercise.name` po starcie sesji nie może modyfikować trwającej sesji ani historii. F5 flagował to jako kandydat ADR (`docs/05-domain.md:20`).

## Decyzja

`WorkoutSession.start()` kopiuje `sets`, `repsMin`, `repsMax`, `weight` z `PlannedExercise` do `SessionExerciseSnapshot`. `exerciseNameSnapshot: String` zamrażany przy starcie. Soft-deleted Exercise renderowany z snapshotu (read-only). Edycja planu lub rename Exercise nie tyka istniejących sesji.

## Konsekwencje

Historia 100% samowystarczalna — eksport JSON nie wymaga joinów. Koszt: duplikacja stringów (akceptowalne — sesje to log, nie source-of-truth). Rename Exercise propaguje w bibliotece i przyszłych sesjach; historyczne sesje zachowują starą nazwę (per PRD US-1 doprecyzowane w F5 review).
